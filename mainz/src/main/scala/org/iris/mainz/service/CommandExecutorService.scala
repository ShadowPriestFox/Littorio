package org.iris.mainz.service

import org.iris.mainz.domain.ExecuteContext
import org.iris.mainz.domain.ProcessInstanceInfo
import org.iris.mainz.domain.ExecutorResponse
import cats.effect.kernel.Async

trait CommandExecutorService[F[_]]:
  def execute(context: ExecuteContext,info: ProcessInstanceInfo,topicName: String): F[ExecutorResponse]

object CommandExecutorService:
  def make[F[_]: Async] = new CommandExecutorService[F] {
    override def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] = ???
  }
