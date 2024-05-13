package org.iris.mainz.service

import cats.effect.IO
import fs2.io.file.Path
import munit.CatsEffectSuite
import cats.implicits.*
import fs2.text
import io.circe.parser.*
import org.iris.mainz.domain.ExecutionSample

import java.io.InputStream


class CommandExecutorServiceSpec extends CatsEffectSuite:
  test("file should be parsed") {
    val st: IO[InputStream] = getClass.getResourceAsStream("/cmd-executor-service-test").pure
    val l = fs2.io.readInputStream(st, 4096, true)
      .through(text.utf8.decode)
      .through(text.lines)
      .evalMap(line => IO(line.substring(line.indexOf('{'))))
      .compile.toList
    val svc = CommandExecutorService.make[IO]()
    val result = for
      ls <- l
      ess <- ls.map(x => IO.fromEither(decode[ExecutionSample](x))).sequence
      r <- ess.map(svc.exam).pure[IO]
    yield r
    assertIO_(result.flatMap(IO.println))
  }


