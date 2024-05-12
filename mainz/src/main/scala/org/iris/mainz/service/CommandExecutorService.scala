package org.iris.mainz.service

import org.iris.mainz.domain.{ExecuteContext, ExecutionSample, ExecutorError, ExecutorResponse, ProcessInstanceInfo, Task}
import cats.effect.Async
import cats.implicits.*
import io.circe.Json
import io.circe.literal.*
import io.circe.parser.*
import io.circe.syntax.*

import scala.jdk.CollectionConverters.*

trait CommandExecutorService[F[_]]:
  def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse]

object CommandExecutorService:
  def make[F[_] : Async](taskService: TaskService[F], commandConnectorService: CommandConnectorService[F], cmdFactory: GroovyCommandFactory[F]) = new CommandExecutorService[F]:
    override def execute(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      for
        task <- taskService.findByTopicAndFlow(topicName, info.processDefinitionId)
        resp <- executeTask(task)
      yield resp

    private def executeTask(task: Task)(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      val commandId = task.connectorId
      val composeId = task.composeConnectorId
      val mappingId = task.mappingId
      (commandId, composeId, mappingId) match
        case (Some(id), _, _) => executeCommand(id, Map.empty)(???)
        case (_, Some(id), _) => ???
        case (_, _, Some(id)) => ???
        case _                => ExecutorResponse(ExecuteContext.default, ExecutorError("PGoPlusException", "", "task do not have connector binding")).pure

    private def executeCommand(id: String, credentialMap: Map[String, String])(contextProcessor: (ExecuteContext, List[String], List[String]) => Json)(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      ???

    private def exam(sample: ExecutionSample) = ???

    private def decrypt(parameters: Map[String, String], parameterKeys: List[Json]): Map[String, String] =
      parameters.map:
        case (k, v) =>
          val pk = parameterKeys.find:
            pk =>
              pk.hcursor.downField("type").as[String].getOrElse("") == "Password" && pk.hcursor.downField("name").as[String].getOrElse("") == k
          k -> pk.map(_ => v /* password decode*/).getOrElse(v)

    private def preCommandExecute(sample: ExecutionSample): (String, Map[String, String]) =
      val parameters = decrypt(sample.parameterInput.get, sample.parameterKeys.get)
      if sample.isCompose then
        val c = decode[Map[String,String]](sample.credential.get.toString).getOrElse(Map.empty)
        val mergedParameters = parameters ++ c
        (sample.asJson.toString, mergedParameters)
      else
        ???

    private def preMappingExecute(sample: ExecutionSample): String = ???
