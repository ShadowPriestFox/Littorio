package org.iris.mainz.domain

case class Task(
    id: String,
    topic: String,
    retry: Int,
    timeout: Int,
    description: Option[String],
    connectorId: Option[String],
    flowId: String,
    mappingId: Option[String],
    composeConnectorId: Option[String],
    credentialId: Option[String]
)
