import sbt._

class UtilProject(info: ProjectInfo) extends ParentProject(info) with IdeaProject {



  lazy val data = project("data")
  //  , "util.data", info => new Specs2Support(info) with IdeaProject {
  //
  //  })

  lazy val io = project("io")
  //  , "util.io", info => new Specs2Support(info) with IdeaProject {
  //
  //    val htmlCleaner = "org.htmlcleaner" % "htmlcleaner" % "2.1"
  //    val xstream = "com.thoughtworks.xstream" % "xstream" % "1.3.1"
  //  })

  lazy val ui = project("ui")

  //  , info => new Specs2Support(info) with IdeaProject {
  //    //val treewrap = "Ken Scambler" %% "scalaswingtreewrapper" % "1.1"
  //    val miglayout = "com.miglayout" % "miglayout" % "3.7.3.1"
  //    val swing= "org.scala-lang" % "scala-swing" % buildScalaVersion
  //    val reactive= "cc.co.scala-reactive" %% "reactive-core" % "0.0.1-SNAPSHOT"
  //
  //  })

//  class Specs2Support(info: ProjectInfo) extends DefaultProject(info) {
//    self: DefaultProject =>
//    val myspecs = specs
//
//    override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
//  }


}
