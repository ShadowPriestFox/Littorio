package org.iris.mainz

import doobie.*
import io.circe.{Decoder, Encoder, Json}
import io.circe.parser.*
import cats.implicits.*
import io.circe.syntax.*

package object implicits:
  given Put[Json] = Put[String].tcontramap(j => j.toString)
  given Get[Json] = Get[String].tmap(str => parse(str).leftMap(e => throw e).merge)
  def encoderPutT[A: Encoder]: Put[A] = Put[Json].tcontramap(_.asJson)
  def decoderGetT[A: Decoder]: Get[A] = Get[Json].temap(_.as[A].leftMap(_.show))