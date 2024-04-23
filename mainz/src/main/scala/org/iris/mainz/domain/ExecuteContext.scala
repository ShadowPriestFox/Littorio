package org.iris.mainz.domain

import io.circe.Json

final case class ExecuteContext(flowContext: Json, flowData: Map[String, Json])
