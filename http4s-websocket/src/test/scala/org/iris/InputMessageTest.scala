package org.iris

import cats.effect.{IO, Ref, Resource}
import munit.CatsEffectSuite
import cats.implicits._

class InputMessageTest extends CatsEffectSuite {
  private val res = Resource.make(
    for cs <- Ref.of[IO, ChatState](ChatState(Map.empty, Map.empty))
        protocol <- IO(Protocol.make(cs))
        im <- IO(InputMessage.make(protocol))
    yield im
  )(_ => IO.unit)
  private val fixture = ResourceSuiteLocalFixture(
    "input-message",
    res
  )
  private val emptyUserFixture = ResourceSuiteLocalFixture(
    "ref-user",
    Resource.make(Ref.of[IO,Option[User]](new User("hxx").some)):_ =>
      IO.unit
  )

  override def munitFixtures: Seq[Fixture[_]] = List(fixture,emptyUserFixture)

  test("/help should be parse correct") {
    val text ="""Commands:
        | /help                 - Show this  text
        | /room                 - Change to default/entry room
        | /room <room name>     - Change to specified room
        | /rooms                - List all rooms
        | /members              - List members in current room
        """.stripMargin
    (for im <- IO(fixture())
      user <- IO(emptyUserFixture())
      messages <- im.parse(user,"/help")
      u <- user.get
      r <- List(SendToUser(u.get,text)).pure[IO]
    yield {
      messages == r
    }).assert

  }

}
