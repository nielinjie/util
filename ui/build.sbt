name := "util.ui"

version := "1.0"

organization := "nielinjie"

scalaVersion := "2.9.1"

resolvers += ScalaToolsSnapshots

resolvers += "wso" at "http://dist.wso2.org/maven2/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"



libraryDependencies += "org.specs2" %% "specs2" % "latest.release" % "test"

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "latest.release",
    "com.miglayout" % "miglayout" % "3.7.3.1",
    "cc.co.scala-reactive" %% "reactive-core" % "0.2-SNAPSHOT"
)

libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
	deps :+ ("org.scala-lang" % "scala-swing" % sv)
}

checksums := Nil

fork in run := true
