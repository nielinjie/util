import sbt._

class UiProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  val akkaRepository = "akka repository" at "http://www.scalablesolutions.se/akka/repository/"
  val ibiblioRepository = "ibiblio repository" at "http://www.ibiblio.org/maven/"
  val wsoRepository = "wso" at "http://dist.wso2.org/maven2/"

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  val specs = "org.specs2" %% "specs2" % "1.1-SNAPSHOT" % "test"

  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  val miglayout = "com.miglayout" % "miglayout" % "3.7.3.1"
  val swing = "org.scala-lang" % "scala-swing" % buildScalaVersion
  val reactive = "cc.co.scala-reactive" %% "reactive-core" % "0.0.1-SNAPSHOT"
}