name := "dist-cache"
version := "0.1"
scalaVersion := "3.1.3"

import scala.sys.process.Process

/** API for public interfaces */
lazy val `dist-cache-api` = project
  .settings(
    description := "Api for dist cache"
  )

/** library to be used in any application */
lazy val `dist-cache-lib` = project
  .settings(
    description := "Library to be used outside in any application"
  )
  .dependsOn(`dist-cache-api`)
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-core" % "1.4.4",
      "ch.qos.logback" % "logback-classic" % "1.4.4",
      "org.slf4j" % "slf4j-api" % "2.0.3"
    ),
    fork := true
  )

/** standalone application with HTTP interface for cache read/write */
lazy val `dist-cache-app` = project
  .settings(
    description := "Standalone application to access cache items on external storages or  "
  )
  .dependsOn(`dist-cache-api`)
  .dependsOn(`dist-cache-lib`)

/** client for HTTP/Socket connection to Cache Manager or Cache Application */
lazy val `dist-cache-client` = project
  .settings(
    description := "client for HTTP/Socket connection to Cache Manager or Cache Application"
  )
  .dependsOn(`dist-cache-api`)
