import sbt._

object Dependencies {

  val distCacheApi: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native" % "3.6.0-M2",
    "org.json4s" %% "json4s-jackson" % "",
    "commons-dbcp" % "commons-dbcp" % "1.4",
    "org.apache.httpcomponents" % "httpclient" % "4.5"
  )

  val distCacheLib: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native" % "3.6.0-M2",
    "org.json4s" %% "json4s-jackson" % "",
    "commons-dbcp" % "commons-dbcp" % "1.4",
    "org.apache.httpcomponents" % "httpclient" % "4.5"
  )

  val distCacheApp: Seq[ModuleID] = Seq(
    "org.json4s" %% "json4s-native" % "3.6.0-M2",
    "org.json4s" %% "json4s-jackson" % "",
    "commons-dbcp" % "commons-dbcp" % "1.4",
    "org.apache.httpcomponents" % "httpclient" % "4.5"
  )

}
