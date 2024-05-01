package org.iris.mainz.service

import org.iris.mainz.domain.ExecuteContext
import org.iris.mainz.domain.ProcessInstanceInfo
import org.iris.mainz.domain.ExecutorResponse
import cats.effect.Async
import cats.implicits.*

trait CommandExecutorService[F[_]]:
  def execute(context: ExecuteContext,info: ProcessInstanceInfo,topicName: String): F[ExecutorResponse]

object CommandExecutorService:
  def make[F[_]: Async](taskService: TaskService[F]) = new CommandExecutorService[F] {
    override def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      for
        task <- taskService.findByTopicAndFlow(topicName,info.processDefinitionId)
      yield ???

    def executeCommand(commandId: String,info: ProcessInstanceInfo) = ???
  }


