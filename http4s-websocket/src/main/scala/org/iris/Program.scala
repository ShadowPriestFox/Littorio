package org.iris

import cats.effect.IOApp
import cats.effect.IO
import org.iris.Server.server
import cats.effect.std.Queue
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Topic
import fs2.Stream 
import concurrent.duration.DurationInt
import cats.effect.kernel.Ref

object  Program extends IOApp.Simple{
  override def run: IO[Unit] = for {
    q <- Queue.unbounded[IO, OutputMessage]
    t <- Topic[IO, OutputMessage]
    cs <- Ref.of[IO, ChatState](ChatState(Map.empty,Map.empty))
    protocol <- IO(Protocol.make[IO](cs))
    im <- IO(InputMessage.make[IO](protocol))
    s <- Stream(
      Stream.fromQueueUnterminated(q).through(t.publish),
      Stream.awakeEvery[IO](30.seconds).map(_ => KeepAlive).through(t.publish),
      Stream.eval(server[IO](q,t,im,protocol,cs))
    ).parJoinUnbounded.compile.drain
  } yield s
}
