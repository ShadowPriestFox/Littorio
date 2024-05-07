package org.iris.mainz.domain

import io.circe.Json

case class CommandConnector(id: String,name: String,bp: String, command: String, parameters: Option[Json], dataField: Option[Json], variable: Option[Json]) 

