val ScalatraVersion = "2.6.3"

organization := "com.hyf"

name := "nickname-manager"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container;compile",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

  //json
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s" %% "json4s-jackson" % "3.2.11",

  //mongo
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.json4s" %% "json4s-mongo" % "3.5.4",

  "org.mockito" %% "mockito-scala" % "0.4.4",
  // https://mvnrepository.com/artifact/com.github.fakemongo/fongo
  "com.github.fakemongo" % "fongo" % "2.1.1" % Test


)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)


