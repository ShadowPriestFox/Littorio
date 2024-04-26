package org.iris.mainz.domain

import io.circe.Codec
import io.circe.generic.semiauto.*
import cats.effect.Concurrent
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

final case class ProcessInstanceInfo(
    taskId: String,
    processDefinitionKey: String,
    processDefinitionId: String,
    businessKey: String,
    instanceId: String
)

object ProcessInstanceInfo:
  given Codec[ProcessInstanceInfo] = deriveCodec
  given [F[_]: Concurrent]: EntityDecoder[F, ProcessInstanceInfo] =
    jsonOf[F, ProcessInstanceInfo]
