package org.iris

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import tyrian.TyrianApp
import tyrian.Cmd
import cats.effect.IO
import tyrian.Html
import tyrian.Sub

trait Msg
trait Model

@JSExportTopLevel("IrisApp")
object App extends TyrianApp[Msg, Model]:

  override def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) = ???

  override def view(model: Model): Html[Msg] = ???

  override def update(model: Model): Msg => (Model, Cmd[IO, Msg]) = ???

  override def subscriptions(model: Model): Sub[IO, Msg] = ???
    

  
