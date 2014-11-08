
name := "util.cloud"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4","2.11.2")


resolvers +="baidu" at "http://maven.duapp.com/nexus/content/repositories/releases/"

libraryDependencies ++= Seq(
    "net.databinder" %% "unfiltered"        % "0.8.+",
    "net.databinder" %% "unfiltered-jetty"  % "0.8.+",
    "net.databinder" %% "unfiltered-filter" % "0.8.+",
    "net.databinder" %% "unfiltered-util"   % "0.8.+",
    "net.databinder" %% "unfiltered-json4s"   % "0.8.+",
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "org.mongodb" %% "casbah-core" % "2.7.3",
    "org.json4s" %% "json4s-mongo" % "3.2.10",
    "org.json4s" %% "json4s-ext" % "3.2.10",
    "org.json4s" %% "json4s-scalaz" % "3.2.10",
    "com.github.nscala-time" %% "nscala-time" % "1.4.0",
    "com.escalatesoft.subcut" %% "subcut" % "2.1",
    "net.databinder" %% "dispatch-http" % "0.8.10",
    "net.databinder" %% "dispatch-http-json" % "0.8.10",
    "nielinjie" %% "util-data" % "1.0",
    "com.baidu.bae" % "baev3-sdk" % "1.0.1",
    "org.slf4j" % "slf4j-log4j12" % "1.7.7",
    "net.databinder" %% "unfiltered-specs2" % "0.8.+" % "test",
    "com.github.athieriot" %% "specs2-embedmongo" % "0.7.0" % "test"
)
