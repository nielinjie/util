import sbt._
import de.element34.sbteclipsify._

class DataProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject with Eclipsify{
  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
  //val akkaRepository = "akka repository" at "http://www.scalablesolutions.se/akka/repository/"
  //val wsoRepository = "wso" at "http://dist.wso2.org/maven2/"
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  val specs = "org.specs2" %% "specs2" % "latest.release" % "test"

  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  val scalazCore = "org.scalaz" %% "scalaz-core" % "latest.release"

}