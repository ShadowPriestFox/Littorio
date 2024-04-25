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
import org.iris.mainz.module.DBResources

object MainzServer:

  def run[F[_]: Async: Network: LoggerFactory]: F[Unit] = 
    val logger = LoggerFactory[F].getLogger
    (for 
      config <- Resource.pure(pureconfig.ConfigSource.default.loadOrThrow[ServerConf])
      (db,kernel) <- DBResources.make(config.db)
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
    yield (config,s,db)).use[Unit]:
      case (config,s,db) => 
        for
          _ <- logger.info(s"server started: $config")
          r <- DBResources.connected(db)
          _ <- logger.info(s"db connected: $r")
          _ <- Async[F].never
        yield ()
