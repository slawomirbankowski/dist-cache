name := "dist-cache"
version := "0.1"
scalaVersion := "3.1.3"

import scala.sys.process.Process

lazy val `dist-cache-api` = project
  .settings(
    description := "Api for dist cache"
  )

lazy val `dist-cache-lib` = project
  .settings(
    description := "Library to be used outside in any application"
  )

lazy val `dist-cache-app` = project
  .settings(
    description := "Standalone application "
  )
  .dependsOn(`dist-cache-api`)
  .dependsOn(`dist-cache-lib`)
