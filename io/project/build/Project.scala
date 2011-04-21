import sbt._

class IoProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  val akkaRepository = "akka repository" at "http://www.scalablesolutions.se/akka/repository/"
  val ibiblioRepository = "ibiblio repository" at "http://www.ibiblio.org/maven/"
  val wsoRepository = "wso" at "http://dist.wso2.org/maven2/"
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  val specs = "org.specs2" %% "specs2" % "1.1" % "test"

  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  val htmlCleaner = "org.htmlcleaner" % "htmlcleaner" % "2.1"
  val xstream = "com.thoughtworks.xstream" % "xstream" % "1.3.1"
}