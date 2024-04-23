package org.iris.mainz

import cats.effect.Async
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.LoggerFactory
import cats.effect.kernel.Resource
import org.iris.mainz.conf.ServerConf
import cats.implicits.*

object MainzServer:

  def run[F[_]: Async: Network: LoggerFactory]: F[Unit] = 
    val logger = LoggerFactory[F].getLogger
    (for 
      config <- Resource.pure(pureconfig.ConfigSource.default.loadOrThrow[ServerConf])
      s <- 
        for
          client <- EmberClientBuilder.default[F].build
          helloWorldAlg = HelloWorld.impl[F]
          jokeAlg = Jokes.impl[F](client)
          httpApp = (
            MainzRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
            MainzRoutes.jokeRoutes[F](jokeAlg)
          )
          finalApp = Logger.httpApp(true, true)(httpApp.orNotFound)
          server <- EmberServerBuilder.default[F].withHost(ipv4"0.0.0.0").withPort(port"8080").withHttpApp(finalApp).build
        yield server
    yield (config,s)).use[Unit]:
      case (config,s) => 
        for
          _ <- logger.info(s"server started: $config")
          _ <- Async[F].never
        yield ()

  //   for {
  //     client <- EmberClientBuilder.default[F].build
  //     helloWorldAlg = HelloWorld.impl[F]
  //     jokeAlg = Jokes.impl[F](client)

  //     // Combine Service Routes into an HttpApp.
  //     // Can also be done via a Router if you
  //     // want to extract segments not checked
  //     // in the underlying routes.
  //     httpApp = (
  //       MainzRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
  //       MainzRoutes.jokeRoutes[F](jokeAlg)
  //     ).orNotFound

  //     // With Middlewares in place
  //     finalHttpApp = Logger.httpApp(true, true)(httpApp)

  //     _ <- 
  //       EmberServerBuilder.default[F]
  //         .withHost(ipv4"0.0.0.0")
  //         .withPort(port"8080")
  //         .withHttpApp(finalHttpApp)
  //         .build
  //   } yield ()
  // }.useForever
