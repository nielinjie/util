package nielinjie
package util.ui
package example

import swing._
import event.{WindowClosed, EditDone, SelectionChanged}
import util.ui.event.SelectSupport
import reactive.Observing

object Tables extends SimpleSwingApplication with ListAndText {
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

trait ListAndText extends Observing{
  self: Tables.type =>
//  import UISupport._
//
//  val strings = List("a", "b", "c")
//  val masterAndD = new MasterDetail(Model(strings))
//
//  val listView = masterAndD.bindMasterTo(new ListView[String]())
//  //masterAndD.supported(BindMaster(listView), )
//  val button = new Button("save")
//  val textBox = new TextField()
//  masterAndD.bindDetail {
//    textBox.text = _
//  }
//  masterAndD.detailBack(textBox, {
//    case (EditDone(`textBox`)) =>
//      textBox.text
//  })
  val listView=new ListView[String](List("A","B","C"))
  SelectSupport.singleSelect(listView).selectChangedEvent.foreach(println)

}