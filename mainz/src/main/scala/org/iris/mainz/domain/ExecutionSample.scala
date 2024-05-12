package org.iris.mainz.domain

import io.circe.Json

case class ExecutionSample(credential: Option[Json],
                           topics: List[String],
                           variables: List[String],
                           parameterInput: Option[Map[String, String]],
                           parameterOutput: Option[Json],
                           parameterKeys: Option[List[Json]],
                           runtimeContext: ExecuteContext,
                           isCompose: Boolean,
                           mappingInput: Option[String],
                           commandInput: Option[String]
                          )
