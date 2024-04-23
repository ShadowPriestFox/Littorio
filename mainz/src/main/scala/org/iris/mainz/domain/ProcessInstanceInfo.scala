package org.iris.mainz.domain

final case class ProcessInstanceInfo(
    taskId: String,
    processDefinitionKey: String,
    processDefinitionId: String,
    businessKey: String,
    instanceId: String
)
