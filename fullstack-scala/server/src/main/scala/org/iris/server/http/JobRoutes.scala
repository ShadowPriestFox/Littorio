package org.iris.server.http

import cats.effect.*
import org.iris.server.core.Jobs
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import org.iris.domain.Job
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.*
import cats.implicits._
import org.http4s.server.Router

class JobRoutes[F[_]: Concurrent] private (jobs: Jobs[F]) extends Http4sDsl[F]:
  private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F]:
    case req @ POST -> Root / "create" => 
      for job <- req.as[Job] 
        id <- jobs.create(job)
        resp <- Created(id)
      yield resp
  private val getAllRoutes: HttpRoutes[F] = HttpRoutes.of[F]:
    case GET -> Root => jobs.all.flatMap(Ok(_))
  
  val routes: HttpRoutes[F] = Router(
    "/jobs" -> (createJobRoute <+> getAllRoutes)
  )

object JobRoutes:
  def resource[F[_]: Concurrent](jobs: Jobs[F]): Resource[F, JobRoutes[F]] = 
    Resource.pure(new JobRoutes[F](jobs))

end JobRoutes