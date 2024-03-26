package org.iris

import cats.data.Validated
import cats.effect.kernel.Ref
import cats.Monad
import cats.implicits._
import cats.parse.Parser
import cats.parse.Parser.char
import cats.parse.Rfc5234.{alpha, sp, wsp}
import cats.Applicative
import cats.data.Validated.Valid
import cats.data.Validated.Invalid

case class TextCommand(left: String,right: Option[String])
trait InputMessage[F[_]]:
  def defaultRoom: Validated[String, Room]
  def parse(userRef: Ref[F, Option[User]], text: String): F[List[OutputMessage]]

object InputMessage:
  private def processText4Reg[F[_]: Applicative](user: User, text: String, protocol: Protocol[F]): F[List[OutputMessage]] =
    if text.charAt(0) == '/' then
      parseToCommand(text).fold(_ => List(ParsingError(None,"Character after '/' must be between A-Z or a-z")).pure[F], 
        {
          case TextCommand("/name", Some(n)) => List(ParsingError(Some(user), "You can't register again")).pure[F]
          case TextCommand("/room", Some(r)) => Room(r) match
            case Valid(a) => protocol.enterRoom(user,a)
            case Invalid(e) =>List(ParsingError(Some(user), e)).pure[F]
          case TextCommand("/help", None) => protocol.help(user).map(List(_))
          case TextCommand("/rooms", None) => protocol.listRooms(user)
          case TextCommand("/members", None) => protocol.listMembers(user)
          case _ => List(UnsupportedCommand(user.some)).pure[F]
        })
    else
      protocol.chat(user, text)
  private def processText4UnReg[F[_]: Monad](text: String, protocol: Protocol[F], userRef: Ref[F, Option[User]], room: Room) = 
    if text.charAt(0) == '/' then
      parseToCommand(text).fold(_ => List(ParsingError(None,"Characters offer '/' must be between A-Z or a-z")).pure[F],
        {
          case TextCommand("/name", Some(n)) => protocol.isUsernameInUse(n).flatMap(b => 
            if b then
              List(ParsingError(None,"User name already in use")).pure[F]
            else
              protocol.register(n).flatMap{
                case SuccessfulRegistration(user, msg) => 
                  for _ <- userRef.update(_ => Some(user))
                    om <- protocol.enterRoom(user, room)
                  yield List(SendToUser(user, "/help show all available commands")) ++ om
                case parsingError @ ParsingError(_, _) => List(parsingError).pure[F]
                case _ => List.empty[OutputMessage].pure[F]
              }
            )
          case _ => List(UnsupportedCommand(None)).pure[F]
        }
      )
    else
      List(Register(None)).pure[F]
  private def commandParser: Parser[TextCommand] =
    val leftSide = ((char('/').string) ~ alpha.rep.string).string
    val rightSide: Parser[(Unit, String)] = sp ~ alpha.rep.string
    ((leftSide ~ rightSide.?) <* wsp.rep.?).map((left,right) => TextCommand(left, right.map((_, str) => str)))
  
  private def parseToCommand(value: String): Either[Parser.Error, TextCommand] = commandParser.parseAll(value)

  def make[F[_]: Monad](protocol: Protocol[F]): InputMessage[F] = 
    new InputMessage[F]:
      override def parse(userRef: Ref[F, Option[User]], text: String): F[List[OutputMessage]] = 
        text.trim() match
          case "" => List(DiscardMessage).pure[F]
          case txt => userRef.get.flatMap{u =>
            u.fold{
              ???
            }{
              ???
            }
            }
        
      override def defaultRoom: Validated[String, Room] = ???
