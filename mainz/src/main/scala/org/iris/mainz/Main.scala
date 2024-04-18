package org.iris.mainz

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = MainzServer.run[IO]
