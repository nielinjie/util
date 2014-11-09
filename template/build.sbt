//XXX
name := "none"

version := "0.1"

scalaVersion := "2.11.2"

resolvers += "baidu" at "http://maven.duapp.com/nexus/content/repositories/releases/"

libraryDependencies ++= Seq(
  "nielinjie" %% "util-cloud" % "1.0",
  "nielinjie" %% "util-test" % "1.0" % "test"
)


jetty()
