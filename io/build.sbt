name := "util.io"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.8.1"

resolvers += ScalaToolsSnapshots

resolvers += "wso" at "http://dist.wso2.org/maven2/"



libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

libraryDependencies ++= Seq(
	 "org.scalaz" %% "scalaz-core" % "latest.release",
	"org.htmlcleaner" % "htmlcleaner" % "2.1",
	"com.thoughtworks.xstream" % "xstream" % "1.3.1"
)
