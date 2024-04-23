package org.iris.mainz

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp.Simple:
  given LoggerFactory[IO] = Slf4jFactory.create
  val run = MainzServer.run[IO]
