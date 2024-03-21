package org.iris

import cats.effect.IOApp
import cats.effect.IO
import org.iris.Server.server

object  Program extends IOApp.Simple{
  override def run: IO[Unit] = server[IO] 
}
