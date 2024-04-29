package org.iris.mainz.domain

import io.circe.Codec
import cats.effect.Concurrent
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

final case class ProcessInstanceInfo(
    taskId: String,
    processDefinitionKey: String,
    processDefinitionId: String,
    businessKey: String,
    instanceId: String
)derives Codec.AsObject

object ProcessInstanceInfo:
  given [F[_]: Concurrent]: EntityDecoder[F, ProcessInstanceInfo] =
    jsonOf[F, ProcessInstanceInfo]
