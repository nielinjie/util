name := "util.data"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.9.0-1"

resolvers += ScalaToolsSnapshots

libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

libraryDependencies ++= Seq(
	 "org.scalaz" %% "scalaz-core" % "latest.release"
)
