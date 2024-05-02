package org.iris.mainz.service

import org.iris.mainz.domain.{ExecuteContext, ExecutorError, ExecutorResponse, ProcessInstanceInfo, Task}
import cats.effect.Async
import cats.implicits.*

trait CommandExecutorService[F[_]]:
  def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse]

object CommandExecutorService:
  def make[F[_] : Async](taskService: TaskService[F]) = new CommandExecutorService[F]:
    override def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      for
        task <- taskService.findByTopicAndFlow(topicName, info.processDefinitionId)
        resp <- executeTask(task)
      yield resp

    private def executeTask(task: Task): F[ExecutorResponse] =
      val commandId = task.connectorId
      val composeId = task.composeConnectorId
      val mappingId = task.mappingId
      (commandId, composeId, mappingId) match
        case (Some(id), _, _) => executeCommand(id)
        case (_, Some(id), _) => ???
        case (_, _, Some(id)) => ???
        case _                => ExecutorResponse(ExecuteContext.default, ExecutorError("PGoPlusException", "", "task do not have connector binding")).pure

    private def executeCommand(id: String): F[ExecutorResponse] = ???
  


