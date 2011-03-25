import sbt._

class UtilProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject {

  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  val akkaRepository = "akka repository" at "http://www.scalablesolutions.se/akka/repository/"
  val ibiblioRepository = "ibiblio repository" at "http://www.ibiblio.org/maven/"
  val wsoRepository = "wso" at "http://dist.wso2.org/maven2/"

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  val specs = "org.specs2" %% "specs2" % "1.1-SNAPSHOT" % "test"


  lazy val data = project("data", "util.data", info => new Specs2Support(info) with IdeaProject {

  })

  lazy val io = project("io", "util.io", info => new Specs2Support(info) with IdeaProject {

    val htmlCleaner = "org.htmlcleaner" % "htmlcleaner" % "2.1"
    val xstream = "com.thoughtworks.xstream" % "xstream" % "1.3.1"
  })

  lazy val ui = project("ui", "util.ui", info => new Specs2Support(info) with IdeaProject {
    //val treewrap = "Ken Scambler" %% "scalaswingtreewrapper" % "1.1"
    val miglayout = "com.miglayout" % "miglayout" % "3.7.3.1"
    val swing= "org.scala-lang" % "scala-swing" % "2.9.0-SNAPSHOT"

  })

  class Specs2Support(info: ProjectInfo) extends DefaultProject(info) {
    self: DefaultProject =>
    val myspecs = specs

    override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
  }


}