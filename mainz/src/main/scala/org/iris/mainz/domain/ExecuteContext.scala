package org.iris.mainz.domain

import io.circe.Json
import io.circe.Codec
import io.circe.generic.semiauto.*
import org.http4s.EntityDecoder
import cats.effect.Concurrent
import org.http4s.circe.jsonOf
import io.circe.literal.*

final case class ExecuteContext(context: Json, flowData: Map[String, Json])
    derives Codec.AsObject

object ExecuteContext:
  given [F[_]: Concurrent]: EntityDecoder[F, ExecuteContext] =
    jsonOf[F, ExecuteContext]

  def default: ExecuteContext = ExecuteContext(json"{}", Map.empty)