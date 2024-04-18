val scala3Version = "3.4.1"
val Fs2Version = "3.2.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "fs2-tutorial",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "co.fs2" %% "fs2-core" % Fs2Version
  )
