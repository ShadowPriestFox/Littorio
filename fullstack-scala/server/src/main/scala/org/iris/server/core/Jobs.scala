package org.iris.server.core

import java.util.UUID
import org.iris.domain.Job
import cats.effect.*
import doobie.util.transactor.Transactor
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.ExecutionContexts
import doobie.hikari.HikariTransactor
import cats.implicits.*

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
  override def create(job: Job): F[UUID] = 
    sql"""
      Insert into jobs(
        company,
        title,
        description,
        exteranlUrl,
        salaryLo,
        salaryHi,
        currency,
        remote,
        location,
        country
      ) Values(
        ${job.company},
        ${job.title},
        ${job.description},
        ${job.externalUrl},
        ${job.salaryLo},
        ${job.salaryHi},
        ${job.currency},
        ${job.remote},
        ${job.location},
        ${job.country}
      )
    """.update
      .withUniqueGeneratedKeys[UUID]("id")
      .transact(transactor)
object JobsLive:
  def make[F[_]: Concurrent](postgres: Transactor[F]): F[JobsLive[F]] =
    new JobsLive[F](postgres).pure[F]

  def resource[F[_]: Concurrent](postgres: Transactor[F]): Resource[F, JobsLive[F]] =
    Resource.pure(new JobsLive[F](postgres))
end JobsLive

object JobsPlayground extends IOApp.Simple {
  def makePostgres = for ec <- ExecutionContexts.fixedThreadPool[IO](32)
    transactor <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      "jdbc:postgresql://localhost:5444/",
      "iris",
      "iris",
      ec
    )
    yield transactor
  
  def program(postgres: Transactor[IO]) = 
    for jobs <- JobsLive.make[IO](postgres)
      _ <- jobs.create(Job.dummy)
      list <- jobs.all
      _ <- IO.println(list)
    yield ()

  override def run: IO[Unit] = makePostgres.use(program)
}