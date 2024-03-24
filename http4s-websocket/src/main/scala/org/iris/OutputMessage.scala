package org.iris

sealed trait OutputMessage

case class Register(user: Option[User], msg: String = """|Register your username with the following command:
                                                         |/name <username>""".stripMargin) extends OutputMessage

case class ParsingError(user: Option[User], msg: String) extends OutputMessage

case class SuccessfulRegistration(
  user: User,
  msg: String = ""
) extends OutputMessage

object SuccessfulRegistration{
  def apply(user: User): SuccessfulRegistration = SuccessfulRegistration(user, s"${user.name} entered the chat")
}

case class UnsupportedCommand(
  user: Option[User],
  msg: String = "Unsupported command"
) extends OutputMessage
