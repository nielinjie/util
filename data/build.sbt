//import de.johoop.jacoco4sbt._
//import JacocoPlugin._

name := "util.data"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.10.3"

//resolvers += ScalaToolsSnapshots

libraryDependencies += "org.specs2" %% "specs2" % "2.3.7" % "test" 

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	 "org.scalaz" %% "scalaz-core" % "7.0.3",
	 "cc.co.scala-reactive" %% "reactive-core" % "0.3.0",
	 "org.scala-stm" %% "scala-stm" % "0.7"
)

//seq(jacoco.settings : _*)

//scalacOptions += "-Xprint:typer"
