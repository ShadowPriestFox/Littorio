package org.iris.mainz.service

import cats.effect.{Async, Resource}
import groovy.lang.GroovyClassLoader
import cats.implicits.*
import org.iris.mainz.fake.FakeCommand

trait GroovyCommandFactory[F[_]]:
  def createCommand(code: String, bp: String): F[FakeCommand]

object GroovyCommandFactory:
  def make[F[_] : Async] = new GroovyCommandFactory[F]:
    override def createCommand(code: String, bp: String): F[FakeCommand] = FakeCommand(code, bp).pure

