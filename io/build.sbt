name := "util.io"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.9.1"

resolvers += ScalaToolsSnapshots

libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

libraryDependencies ++= Seq(
	"org.scalaz" %% "scalaz-core" % "latest.release",
	"com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7",
	"org.slf4j" % "slf4j-log4j12" % "1.6.4",
	"com.github.scala-incubator.io" %% "scala-io-core" % "0.3.0",
	"com.github.scala-incubator.io" %% "scala-io-file" % "0.3.0",
	"net.sourceforge.htmlcleaner" % "htmlcleaner" % "2.2",
	"com.thoughtworks.xstream" % "xstream" % "1.3.1"
)
