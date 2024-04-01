package org.iris.server.core

import java.util.UUID
import org.iris.domain.Job
import cats.effect.*
import doobie.util.transactor.Transactor
import doobie.implicits.*
import doobie.postgres.implicits.*

trait Jobs[F[_]]:
  def create(job: Job): F[UUID]
  def all: F[List[Job]]

case class JobsLive[F[_]: Concurrent] private(transactor: Transactor[F]) extends Jobs[F]:
  override def all: F[List[Job]] = 
    sql"""
      SELECT company,title,description,extrenalUrl,salaryLo,salaryHi,currency,remote,location,country
      FROM jobs
    """.query[Job]
      .stream
      .transact(transactor)
      .compile
      .toList
  override def create(job: Job): F[UUID] = ???
