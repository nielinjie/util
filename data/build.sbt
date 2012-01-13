name := "util.data"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.9.1"

resolvers += ScalaToolsSnapshots

libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	 "org.scalaz" %% "scalaz-core" % "latest.release",
	 "cc.co.scala-reactive" %% "reactive-core" % "0.2-SNAPSHOT",
	 "org.scala-tools" %% "scala-stm" % "0.3"
)

//scalacOptions += "-Xprint:typer"