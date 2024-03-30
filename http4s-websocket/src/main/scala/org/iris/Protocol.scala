package org.iris

import cats.Monad
import cats.effect.kernel.Ref
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.implicits._
import cats.Applicative

trait Protocol[F[_]]:
  def register(name: String): F[OutputMessage]
  def isUsernameInUse(name: String): F[Boolean]
  def enterRoom(user: User, room: Room): F[List[OutputMessage]]
  def chat(user: User, text: String): F[List[OutputMessage]]
  def help(user: User): F[OutputMessage]
  def listRooms(user: User): F[List[OutputMessage]]
  def listMembers(user: User): F[List[OutputMessage]]
  def disconnect(userRef: Ref[F, Option[User]]): F[List[OutputMessage]]
  def whisper(form: User,user: User, text: String): F[List[OutputMessage]]

object Protocol:
  def make[F[_]: Monad](chatState: Ref[F, ChatState]): Protocol[F] = 
    new Protocol[F]:
      private def broadcastMessage[F[_]: Applicative](cs: ChatState, room: Room, om: OutputMessage): F[List[OutputMessage]] = 
        cs.roomMembers.getOrElse(room, Set.empty[User]).map{u =>
            om match
              case SendToUser(user, msg) => SendToUser(u, msg)
              case ChatMsg(from, to, msg) => ChatMsg(from, u, msg)
              case _ => DiscardMessage
          }.toList.pure[F]
      private def removeFromCurrentRoom(stateRef: Ref[F, ChatState], user: User): F[List[OutputMessage]] = 
        stateRef.get.flatMap{cs => 
          cs.userRooms.get(user).fold(List.empty[OutputMessage].pure[F]){room =>
            val updateMembers = cs.roomMembers.getOrElse(room, Set()) - user
            stateRef.update(ccs => 
              ChatState(ccs.userRooms - user, if (updateMembers.isEmpty) then 
                  ccs.roomMembers - room
                else
                  ccs.roomMembers + (room -> updateMembers)
                )
              ).flatMap(_ => broadcastMessage(cs,room, SendToUser(user, s"${user.name} has left the ${room.room} room")))
            }
          }
      
      private def addToRoom[F[_]: Monad](stateRef: Ref[F, ChatState], user: User, room: Room): F[List[OutputMessage]] = 
        stateRef.updateAndGet{cs =>
          val updateMemberList = cs.roomMembers.getOrElse(room, Set()) + user
          ChatState(
            cs.userRooms + (user -> room),
            cs.roomMembers + (room -> updateMemberList)
          )
          }.flatMap{
            broadcastMessage(_ , room, SendToUser(user,s"${user.name} has joined the ${room.room} room"))
          }
      override def help(user: User): F[OutputMessage] = 
        val text = """Commands:
        | /help                 - Show this  text
        | /room                 - Change to default/entry room
        | /room <room name>     - Change to specified room
        | /rooms                - List all rooms
        | /members              - List members in current room
        """.stripMargin
        SendToUser(user, text).pure[F]

      override def enterRoom(user: User, room: Room): F[List[OutputMessage]] = chatState.get.flatMap{cs =>
        cs.userRooms.get(user).fold(addToRoom(chatState,user,room))(r => 
          if (r == room) then
            List(SendToUser(user, s"You are already in the ${room.room} room")).pure[F]
          else
            val leaveMessage = removeFromCurrentRoom(chatState, user)
            val enterMessage = addToRoom(chatState, user, room)
            for leave <- leaveMessage
              enter <- enterMessage
            yield leave ++ enter
          )
        }

      override def disconnect(userRef: Ref[F, Option[User]]): F[List[OutputMessage]] = 
        userRef.get.flatMap{
          case Some(value) => removeFromCurrentRoom(chatState, value)
          case None => List.empty[OutputMessage].pure[F]
        }

      override def listRooms(user: User): F[List[OutputMessage]] = 
        chatState.get.map{cs => 
          val roomList = cs.roomMembers.keys.map(_.room).toList.sorted.mkString("Rooms:\n\t","\n\t","")
          List(SendToUser(user, roomList))
        }

      override def listMembers(user: User): F[List[OutputMessage]] = 
        chatState.get.map{cs =>
          val memeberList = cs.userRooms.get(user) match
            case None => "You are not currently in a room"
            case Some(value) => cs.roomMembers.getOrElse(value,Set())
              .map(_.name)
              .toList
              .sorted
              .mkString("Room Members:\n\t", "\n\t","")
          List(SendToUser(user, memeberList))
        }

      override def isUsernameInUse(name: String): F[Boolean] = chatState.get.map(cs => cs.userRooms.keySet.exists(_.name == name))

      override def chat(user: User, text: String): F[List[OutputMessage]] = 
        for cs <- chatState.get
          output <- cs.userRooms.get(user) match
            case None => List(SendToUser(user, "You are not currently in a room")).pure[F]
            case Some(value) => broadcastMessage(cs,value, ChatMsg(user, user/* this user will be replace in broad cast so here is just a holder*/, text))
        yield output

      override def register(name: String): F[OutputMessage] = 
        (User(name) match
          case Valid(a) => SuccessfulRegistration(a)
          case Invalid(e) => ParsingError(None, e.toString()))
        .pure[F]
      override def whisper(from: User,user: User, text: String): F[List[OutputMessage]] = 
        for cs <- chatState.get
        yield 
          if cs.userRooms.keySet.contains(user) then
            List(SendToUser(from,s"You whisper to ${user.name}: $text"),SendToUser(user, s"${from.name} whisper you: $text"))
          else
            List(UnsupportedCommand(from.some,s"${user.name} not exist"))
