package org.iris.mainz.service

import org.iris.mainz.domain.{ExecuteContext, ExecutorError, ExecutorResponse, ProcessInstanceInfo, Task}
import cats.effect.Async
import cats.implicits.*
import com.inossem.pgoplus.core.commandbase.{CommandParams, ParamType}
import io.circe.Json
import io.circe.literal.*

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

    private def executeCommand(id: String, credentialMap: Map[String, String])(contextProcessor: (ExecuteContext,List[String],List[String]) => Json)(using context: ExecuteContext, info: ProcessInstanceInfo, topicName: String): F[ExecutorResponse] =
      for
        command <- commandConnectorService.findCommandConnector(id)
        script = command.command
        processDefinitionId = info.processDefinitionId
        data = context.flowData
        cmd <- cmdFactory.createCommand(script, command.bp)
        paramsKeys = cmd.getParamsKeys
        optParams = command.parameters.flatMap: j =>
          j.asObject.map: jo =>
            jo.toMap.map:
              case (key, value) => key -> paramsKeys.asScala.find(pk => pk.getName == key && pk.getType == ParamType.Password).fold(value.toString)(json => PasswordCodec.decrypt(json.toString))
        parameters = optParams.getOrElse(Map.empty) ++ credentialMap
        commandParams = CommandParams.create(parameters.asJava)
        _ <- Async[F].delay(cmd.init(commandParams))
        field = command.dataField.flatMap(_.as[List[String]].toOption).getOrElse(List.empty)
        variable = command.variable.flatMap(_.as[List[String]].toOption).getOrElse(List.empty)
        data = contextProcessor(context,field,variable)
        content = if command.name == "NoticeCommand" then
          json"""
                {
                  "instanceId":${info.instanceId},
                  "syncData": $data,
                  "pgoURL": ""
                }
              """.toString else
          data.toString
      yield ???
  


