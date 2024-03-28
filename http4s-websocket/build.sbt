val Http4sVersion = "0.23.23"
val CirceVersion = "0.14.6"
val LogbackVersion = "1.4.11"
val CatsParseVersion = "0.3.10"
val JwtHttp4sVersion = "1.2.0"
val JwtScalaVersion = "9.3.0"

val jwtHttp4s = "dev.profunktor" %% "http4s-jwt-auth" % JwtHttp4sVersion
val jwtScala = "com.github.jwt-scala" %% "jwt-core" % JwtScalaVersion
val jwtCirce = "com.github.jwt-scala" %% "jwt-circe" % JwtScalaVersion

lazy val root = (project in file("."))
  .settings(
    organization := "rockthejvm",
    name := "websockets",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.typelevel" %% "cats-parse" % CatsParseVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      jwtHttp4s,
      jwtScala,
      jwtCirce
    )
  )
