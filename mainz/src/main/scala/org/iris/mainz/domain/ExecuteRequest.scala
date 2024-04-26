package org.iris.mainz.domain

import io.circe.Codec
import io.circe.generic.semiauto.*
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import cats.effect.Concurrent

final case class ExecuteRequest(topicName: String, context: ExecuteContext, info: ProcessInstanceInfo)

object ExecuteRequest:
  given Codec[ExecuteRequest] = deriveCodec
  given [F[_]: Concurrent]: EntityDecoder[F, ExecuteRequest] = jsonOf[F, ExecuteRequest]
