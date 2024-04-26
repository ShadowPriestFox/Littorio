package org.iris.mainz

import cats.effect.Sync
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.iris.mainz.service.CommandExecutorService
import org.iris.mainz.domain.ExecuteRequest
import org.http4s.circe.CirceEntityCodec.*
import cats.effect.kernel.Async

object MainzRoutes:

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }

  def executeRoutes[F[_]: Async](cs: CommandExecutorService[F]): HttpRoutes[F] = 
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F]{
      case request@POST -> Root / "execute" =>
        for
          er <- request.as[ExecuteRequest]
          resp <- cs.execute(er.context,er.info,er.topicName)
          resp <- Ok("get request")
        yield resp
    }
