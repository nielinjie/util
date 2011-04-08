package nielinjie
package util.ui
package example

import swing._
import event.{WindowClosed, EditDone, SelectionChanged}
import reactive.Observing
import util.ui.event.{ SelectSupport}

object Tables extends SimpleSwingApplication with ListAndText {
  import SelectSupport._
  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {
      add(listView, "wrap")
      //      add(textBox, "wrap")
      //      add(button, "wrap")
    }

  }

  reactions += {
    case WindowClosed(top) => {
      println("what?")
      System.exit(0)
    }
  }
  //  listenTo(top)

}

trait ListAndText extends Observing {
  self: Tables.type =>

  import SelectSupport._
  val listView = singleSelect(new ListView[String](List("A", "B", "C")))
  listView.selectChangedEvent.foreach(println)

}