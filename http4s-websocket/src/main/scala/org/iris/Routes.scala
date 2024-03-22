package org.iris

import fs2.io.file.Files
import cats.MonadThrow
import cats.effect.Concurrent
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.StaticFile
import fs2.Stream
import fs2.Pipe
import org.http4s.websocket.WebSocketFrame
import org.http4s.server.websocket.WebSocketBuilder2
import cats.effect.std.Queue
import fs2.concurrent.Topic

class Routes[F[_]: Files: Concurrent] extends Http4sDsl[F] {
  def service(wsb: WebSocketBuilder2[F], q: Queue[F, WebSocketFrame], t: Topic[F, WebSocketFrame]): HttpApp[F] = 
    HttpRoutes.of[F]{
      case request @ GET -> Root / "chat.html" => 
        StaticFile.fromPath(
          fs2.io.file.Path(getClass().getClassLoader().getResource("chat.html").getFile()),
          Some(request)
        ).getOrElseF(NotFound())
      case GET -> Root / "ws" => 
        val wrappedQueue: F[Queue[F, WebSocketFrame]] = Queue.unbounded[F, WebSocketFrame]
        val send: Stream[F, WebSocketFrame] = {
          t.subscribe(maxQueued = 1000)
        }
        val receive: Pipe[F, WebSocketFrame, Unit] = {
          _.foreach(q.offer)
        }
        wsb.build(send, receive)
    }.orNotFound

  
}
