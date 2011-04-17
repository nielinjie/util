package nielinjie
package util.ui
package example

import swing._
import event._
import reactive._

object MasterAndDetail extends SimpleSwingApplication with Observing {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:][fill,:300:]", "[fill,:400:]") {
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {
        add(listView, "wrap")
        add(text, "")
      }, "")
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {

      }, "")
    }

  }

  reactions += {

    case WindowClosed(top) => {
      println("closing")
      System.exit(0)
    }
  }

  val masterDetail = new MasterDetail(List("a", "b"))
  masterDetail.detailBind = Some(Bind(
  {
    a => text.text = a
  }, {
    () => text.text
  }
  ))
  val text = new TextField("what")
  val listView = new ListView[String]()

  import SwingSupport._

  bindToListView(masterDetail, listView)
}