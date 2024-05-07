package org.iris.mainz.service

import cats.effect.{Async, Resource}
import com.inossem.pgoplus.core.commandbase.IGCommand
import groovy.lang.GroovyClassLoader
import cats.implicits.*

trait GroovyCommandFactory[F[_]]:
  def createCommand(code: String, bp: String): F[IGCommand]

object GroovyCommandFactory:
  def make[F[_] : Async] = new GroovyCommandFactory[F]:
    override def createCommand(code: String, bp: String): F[IGCommand] =
      val loader = new GroovyClassLoader().pure[F]
      Resource.fromAutoCloseable(loader).use(loader =>
        for
          clazz <- Async[F].delay(loader.parseClass(code).asInstanceOf[Class[IGCommand]])
          cons <- Async[F].delay(clazz.getConstructor())
          cmd <- Async[F].delay(cons.newInstance())
        yield cmd
      )

