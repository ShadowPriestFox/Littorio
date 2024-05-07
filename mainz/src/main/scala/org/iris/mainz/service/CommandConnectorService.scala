package org.iris.mainz.service

import cats.effect.Async
import doobie.util.transactor.Transactor
import org.iris.mainz.domain.CommandConnector
import doobie.*
import doobie.implicits.*
import org.iris.mainz.implicits.given 

trait CommandConnectorService[F[_]]:
  def findCommandConnector(id: String): F[CommandConnector]

object CommandConnectorService:
  def make[F[_] : Async](xa: Transactor[F]): CommandConnectorService[F] = new CommandConnectorService[F]:
    override def findCommandConnector(id: String): F[CommandConnector] =
      sql"select id,name,bp_id,command,parameters,data_field,variable from command_connector where id = $id"
        .query[CommandConnector]
        .unique.transact(xa)
