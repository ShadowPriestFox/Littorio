package org.iris

import cats.effect.kernel.Async
import fs2.io.file.Files
import fs2.io.net.Network
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import cats.implicits.*
import cats.effect.std.Queue
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Topic
import cats.effect.kernel.Ref

object Server {
  def server[F[_]: Async: Files: Network](q: Queue[F, OutputMessage], t: Topic[F, OutputMessage], im: InputMessage[F], protocol: Protocol[F], cs: Ref[F, ChatState]): F[Unit] =
    val host = host"0.0.0.0"
    val port = port"8088"
    EmberServerBuilder.default[F]
      .withHost(host)
      .withPort(port)
      .withHttpWebSocketApp(wsb => new Routes[F]().service(wsb,q,t,im,protocol, cs))
      .build
      .useForever
      .void
  
}
