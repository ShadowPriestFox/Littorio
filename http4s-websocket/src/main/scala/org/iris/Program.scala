package org.iris

import cats.effect.IOApp
import cats.effect.IO
import org.iris.Server.server
import cats.effect.std.Queue
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Topic
import fs2.Stream 

object  Program extends IOApp.Simple{
  override def run: IO[Unit] = for {
    q <- Queue.unbounded[IO, WebSocketFrame]
    t <- Topic[IO, WebSocketFrame]
    s <- Stream(
      Stream.fromQueueUnterminated(q).through(t.publish),
      Stream.eval(server[IO](q,t))
    ).parJoinUnbounded.compile.drain
  } yield s
}
