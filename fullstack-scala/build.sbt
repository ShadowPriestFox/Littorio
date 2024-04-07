ThisBuild / version := "1.0.0"

lazy val iris    = "org.iris"
lazy val scala3Version = "3.3.1"

lazy val core = (crossProject(JSPlatform, JVMPlatform) in file("common"))
  .settings(
    name         := "common",
    scalaVersion := scala3Version,
    organization := iris
  )

lazy val tyrianVersion = "0.6.1"
lazy val fs2DomVersion = "0.1.0"
lazy val circeVersion  = "0.14.0"

lazy val app = (project in file("app"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name         := "app",
    scalaVersion := scala3Version,
    organization := iris,
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "tyrian-io"     % tyrianVersion,
      "com.armanbilge"  %%% "fs2-dom"       % fs2DomVersion,
      "io.circe"        %%% "circe-core"    % circeVersion,
      "io.circe"        %%% "circe-parser"  % circeVersion,
      "io.circe"        %%% "circe-generic" % circeVersion
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings   := true
  )
  .dependsOn(core.js)

lazy val catsEffectVersion          = "3.3.14"
lazy val http4sVersion              = "0.23.15"
lazy val doobieVersion              = "1.0.0-RC1"
lazy val logbackVersion             = "1.4.0"
lazy val slf4jVersion               = "2.0.0"

lazy val server = (project in file("server"))
  .settings(
    name         := "fullstack-scala-server",
    scalaVersion := scala3Version,
    organization := iris,
    libraryDependencies ++= Seq(
      "org.typelevel"         %% "cats-effect"         % catsEffectVersion,
      "org.http4s"            %% "http4s-dsl"          % http4sVersion,
      "org.http4s"            %% "http4s-ember-server" % http4sVersion,
      "org.http4s"            %% "http4s-circe"        % http4sVersion,
      "io.circe"              %% "circe-generic"       % circeVersion,
      "io.circe"              %% "circe-fs2"           % circeVersion,
      "org.tpolecat"          %% "doobie-core"         % doobieVersion,
      "org.tpolecat"          %% "doobie-hikari"       % doobieVersion,
      "org.tpolecat"          %% "doobie-postgres"     % doobieVersion,
      "org.slf4j"              % "slf4j-simple"        % slf4jVersion,
    ),
    Compile / run / mainClass := Some("org.iris.Application")
  )
  .dependsOn(core.jvm)

lazy val root = (project in file("."))
  .aggregate(server)
  .settings(
    addCommandAlias("run","server/run"),
    name:= "fullstack-scala3",
    scalaVersion := scala3Version,
    organization := iris,
  )