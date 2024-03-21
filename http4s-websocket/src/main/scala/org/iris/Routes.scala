package org.iris

import fs2.io.file.Files
import cats.MonadThrow
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.StaticFile

class Routes[F[_]: Files: MonadThrow] extends Http4sDsl[F] {
  def service: HttpApp[F] = 
    HttpRoutes.of[F]{
      case request @ GET -> Root / "chat.html" => 
        StaticFile.fromPath(
          fs2.io.file.Path(getClass().getClassLoader().getResource("chat.html").getFile()),
          Some(request)
        ).getOrElseF(NotFound())
    }.orNotFound

  
}
