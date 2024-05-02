package org.iris.mainz.domain

import io.circe.{Codec, Json}
import org.iris.mainz.implicits.*
import doobie.*

case class CommandConnector(id: String, command: String, parameters: Option[Json], dataField: Option[Json], variable: Option[Json]) derives Codec.AsObject

object CommandConnector:
  given Put[CommandConnector] = encoderPutT[CommandConnector]
  given Get[CommandConnector] = decoderGetT[CommandConnector]
