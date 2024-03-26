package org.iris

import cats.data.Validated
import cats.effect.kernel.Ref
import cats.Monad
import cats.implicits._
import cats.parse.Parser
import cats.parse.Parser.char
import cats.parse.Rfc5234.{alpha, sp, wsp}

case class TextCommand(left: String,right: Option[String])
trait InputMessage[F[_]]:
  def defaultRoom: Validated[String, Room]
  def parse(userRef: Ref[F, Option[User]], text: String): F[List[OutputMessage]]

object InputMessage:
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
