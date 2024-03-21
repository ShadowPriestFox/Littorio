package org.iris

import cats.effect.kernel.Async
import fs2.io.file.Files
import fs2.io.net.Network
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import cats.implicits.*

object Server {
  def server[F[_]: Async: Files: Network]: F[Unit] =
    val host = host"0.0.0.0"
    val port = port"8088"
    EmberServerBuilder.default[F]
      .withHost(host)
      .withPort(port)
      .withHttpApp(new Routes[F]().service)
      .build
      .useForever
      .void
  
}
