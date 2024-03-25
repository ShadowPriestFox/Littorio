package org.iris

import cats.Monad
import cats.effect.kernel.Ref
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.implicits._

trait Protocol[F[_]]:
  def register(name: String): F[OutputMessage]
  def isUsernameInUse(name: String): F[Boolean]
  def enterRoom(user: User, room: Room): F[List[OutputMessage]]
  def chat(user: User, text: String): F[List[OutputMessage]]
  def help(user: User): F[OutputMessage]
  def listRooms(user: User): F[List[OutputMessage]]
  def listMembers(user: User): F[List[OutputMessage]]
  def disconnect(userRef: Ref[F, Option[User]]): F[List[OutputMessage]]

object Protocol:
  def make[F[_]: Monad](chatState: Ref[F, ChatState]): Protocol[F] = 
    new Protocol[F]:

      override def help(user: User): F[OutputMessage] = 
        val text = """Commands:
        | /help                 - Show this  text
        | /room                 - Change to default/entry room
        | /room <room name>     - Change to specified room
        | /rooms                - List all rooms
        | /members              - List members in current room
        """.stripMargin
        SendToUser(user, text).pure[F]

      override def enterRoom(user: User, room: Room): F[List[OutputMessage]] = ???

      override def disconnect(userRef: Ref[F, Option[User]]): F[List[OutputMessage]] = ???

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

      override def chat(user: User, text: String): F[List[OutputMessage]] = ???

      override def register(name: String): F[OutputMessage] = 
        (User(name) match
          case Valid(a) => SuccessfulRegistration(a)
          case Invalid(e) => ParsingError(None, e.toString()))
        .pure[F]
