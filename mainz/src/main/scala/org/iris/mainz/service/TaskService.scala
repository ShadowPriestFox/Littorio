package org.iris.mainz.service

import cats.effect.Async
import doobie.util.transactor.Transactor
import org.iris.mainz.domain.Task
import doobie.*
import doobie.implicits.*

trait TaskService[F[_]]:
  def findByTopicAndFlow(
      topicName: String,
      processDefinitionId: String
  ): F[Task]

object TaskService:
  def make[F[_]: Async](xa: Transactor[F]) = new TaskService[F]:
    override def findByTopicAndFlow(
        topicName: String,
        processDefinitionId: String
    ): F[Task] =
      sql"select id,topic,retry,time_out,description,connector_id,flow_id,mapping_id,compose_connector_id,credential_id from task where topic=$topicName and flow_id=$processDefinitionId"
        .query[Task]
        .unique.transact(xa)
