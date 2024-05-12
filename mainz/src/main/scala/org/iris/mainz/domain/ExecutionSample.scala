package org.iris.mainz.domain

import io.circe.{Codec, Json}

case class ExecutionSample(credential: Option[Json],
                           topics: List[String],
                           variables: List[String],
                           parameterInput: Option[Map[String, String]],
                           parameterOutput: Option[Map[String,String]],
                           parameterKeys: Option[List[Json]],
                           runtimeContext: ExecuteContext,
                           isCompose: Boolean,
                           mappingInput: Option[String],
                           commandInput: Option[String]
                          ) derives Codec.AsObject
