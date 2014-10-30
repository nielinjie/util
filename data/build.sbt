//import de.johoop.jacoco4sbt._
//import JacocoPlugin._

name := "util.data"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4","2.11.2")


//resolvers += ScalaToolsSnapshots

libraryDependencies += "org.specs2" %% "specs2" % "2.4.9-scalaz-7.0.6" % "test"

resolvers += "Scalaz Bintray Repo"  at "http://dl.bintray.com/scalaz/releases"


libraryDependencies ++= Seq(
	 "org.scalaz" %% "scalaz-core" % "7.0.6",
	 "org.scala-stm" %% "scala-stm" % "0.7"
)



//seq(jacoco.settings : _*)

//scalacOptions += "-Xprint:typer"
