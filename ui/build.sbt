name := "util.ui"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.8.1"

resolvers += ScalaToolsSnapshots

resolvers += "wso" at "http://dist.wso2.org/maven2/"

libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "latest.release",
    "com.miglayout" % "miglayout" % "3.7.3.1",
    "cc.co.scala-reactive" %% "reactive-core" % "0.0.1-SNAPSHOT"
)

libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
	deps :+ ("org.scala-lang" % "scala-swing" % sv)
}

fork in run := true