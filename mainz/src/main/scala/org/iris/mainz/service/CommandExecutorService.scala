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
  def exam(sample: ExecutionSample): Boolean
  def execute(context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse]

object CommandExecutorService:
//  def make[F[_] : Async](taskService: TaskService[F], commandConnectorService: CommandConnectorService[F]) = new CommandExecutorService[F]:
  def make[F[_] : Async]() = new CommandExecutorService[F]:
    override def execute(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      ???
//      for
//        task <- taskService.findByTopicAndFlow(topicName, info.processDefinitionId)
//        resp <- executeTask(task)
//      yield resp

    private def executeTask(task: Task)(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      ???
//      val commandId = task.connectorId
//      val composeId = task.composeConnectorId
//      val mappingId = task.mappingId
//      (commandId, composeId, mappingId) match
//        case (Some(id), _, _) => executeCommand(id, Map.empty)(???)
//        case (_, Some(id), _) => ???
//        case (_, _, Some(id)) => ???
//        case _                => ExecutorResponse(ExecuteContext.default, ExecutorError("PGoPlusException", "", "task do not have connector binding")).pure

    private def executeCommand(id: String, credentialMap: Map[String, String])(contextProcessor: (ExecuteContext, List[String], List[String]) => Json)(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      ???

    def exam(sample: ExecutionSample): Boolean =
      if sample.mappingInput.isDefined then
        val result = preMappingExecute(sample)
        parse(sample.mappingInput.get) == parse(result)
      else
        val (content,parameters) = preCommandExecute(sample)
//        println(content)
//        println(sample.commandInput.get)
//        println(parameters)
//        println(sample.parameterOutput.get)
        if sample.commandInput.get == "" then
          content == "" && sample.parameterOutput.get == parameters
        else
          parse(content) == parse(sample.commandInput.get) && sample.parameterOutput.get == parameters

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
        val c = decode[Map[String, String]](sample.credential.get.toString).getOrElse(Map.empty)
        val mergedParameters = parameters ++ c
        (sample.runtimeContext.asJson.toString, mergedParameters)
      else
        transformContext(sample) -> parameters

    private def preMappingExecute(sample: ExecutionSample): String =
      if sample.isCompose then
        sample.runtimeContext.asJson.toString
      else
        transformContext(sample)

    private def transformContext(sample: ExecutionSample): String =
      val topicsLen = sample.topics.length
      val variablesLen = sample.variables.length
      val total = topicsLen + variablesLen
      (total, topicsLen, variablesLen) match
        case (0, _, _) => ""
        case (1, 1, 0) =>
          val a = sample.runtimeContext.context.hcursor.getOrElse[Json](sample.topics.head)(sample.runtimeContext.flowData.getOrElse(sample.topics.head, json"{}"))
          a.getOrElse(json"{}").toString
        case (1, 0, 1) => sample.runtimeContext.flowData.getOrElse(sample.variables.head, json"{}").toString
        case _         =>
          val topicsMap = sample.topics.map: t =>
            val j = sample.runtimeContext.context.hcursor.getOrElse(t)(sample.runtimeContext.flowData.getOrElse(t, json"{}"))
            t -> j.getOrElse(json"{}")
          val variablesMap = sample.variables.map: v =>
            val j = sample.runtimeContext.flowData.getOrElse(v, json"{}")
            v -> j
          val mergedMap = topicsMap.toMap ++ variablesMap.toMap
          mergedMap.asJson.toString
