package org.iris.mainz

import cats.implicits.*
import doobie.*
import io.circe.Json
import io.circe.parser.*

package object implicits:
//  given Put[Json] = Put[String].tcontramap(j => j.toString)
//  given Get[Json] = Get[String].tmap(str => parse(str).leftMap(e => Json.Null).merge)
//  def encoderPutT[A: Encoder]: Put[A] = Put[Json].tcontramap(_.asJson)
//  def decoderGetT[A: Decoder]: Get[A] = Get[Json].temap(_.as[A].leftMap(_.show))
  given Meta[Json] = Meta.Advanced.other[String]("json").timap[Json](a => parse(a).leftMap[Json](e => throw e).merge)(a => a.toString)