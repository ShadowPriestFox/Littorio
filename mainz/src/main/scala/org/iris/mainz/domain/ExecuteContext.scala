package org.iris.mainz.domain

import io.circe.Json
import io.circe.Codec
import io.circe.generic.semiauto.*
import org.http4s.EntityDecoder
import cats.effect.Concurrent
import org.http4s.circe.jsonOf

final case class ExecuteContext(flowContext: Json, flowData: Map[String, Json])

object ExecuteContext:
  given Codec[ExecuteContext] = deriveCodec
  given [F[_]: Concurrent]: EntityDecoder[F, ExecuteContext] = jsonOf[F, ExecuteContext]


