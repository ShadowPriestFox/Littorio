package org.iris.server

import cats.effect.IOApp
import doobie.util.ExecutionContexts
import cats.effect.IO
import doobie.util.transactor
import doobie.hikari.HikariTransactor
import org.iris.server.core.JobsLive
import org.iris.server.http.JobRoutes
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import cats.implicits.*
import org.http4s.server.middleware.CORS

object Application extends IOApp.Simple:
  def makePostgres = 
    for ec <- ExecutionContexts.fixedThreadPool[IO](32)
      transactor <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        "jdbc://postgresql://localhost:5444/",
        "iris",
        "iris",
        ec
      )
    yield transactor

  def makeServer = 
    for postgres <- makePostgres
      jobs <- JobsLive.resource[IO](postgres)
      jobApi <- JobRoutes.resource[IO](jobs)
      server <- EmberServerBuilder.default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"4041")
        .withHttpApp(CORS(jobApi.routes.orNotFound))
        .build
    yield server


  override def run: IO[Unit] = makeServer.use: _ =>
    IO.println("Server ready!") *> IO.never
end Application

