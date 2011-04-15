package nielinjie
package util.ui
package example

import swing._
import event._
import util.ui.event.{EventSupport, SelectSupport}
import reactive.{EventStream, Var, Observing}

object Tables extends SimpleSwingApplication with ListAndText with ComplexList {

  import SelectSupport._

  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:][fill,:300:]", "[fill,:400:]") {
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {
        add(listView, "wrap")
        add(textBox, "wrap")
        add(button, "wrap")
      }, "")
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {
        add(cList, "wrap")
        add(fText, "wrap")
        add(lText, "wrap")
      }, "")
    }

  }

  reactions += {

    case WindowClosed(top) => {
      println("what?")
      System.exit(0)
    }
  }
  //  listenTo(fText)
  //  reactions+={
  //    case EditDone(`fText`)=>println("outter")
  //  }
}

trait ListAndText extends Observing {
  self: Tables.type =>

  import SelectSupport._, EventSupport._

  val listView = new ListView[String](List("A", "B", "C")).singleSelect
  listView.selectChanging.foreach {
    x =>
      println(x.toString)
  }
  listView.selected.change.foreach {
    x =>
      println(x.toString)
  }

  val textBox = new TextField()
  val button = new Button("Change")
  val buttonStream = button.eventStream({
    case ButtonClicked(_) =>
      "foo"
  })
  listView.bindSelected({
    x =>
      textBox.text = x.toString
  }, buttonStream)
}

trait ComplexList extends Observing {

  case class Name(first: String, last: String)

  import SelectSupport._, EventSupport._

  val cList = new ListView[Name](List(Name("nie", "jason"), Name("xu", "elsa"))).singleSelect
  //  cList.selected.change.foreach {
  //    x =>
  //      println(x.toString)
  //  }
  val fText = new TextField()
  val lText = new TextField()

  //  fText.listenTo(fText)
  //  fText.reactions+={
  //    case EditDone(_)=>println("editDone")
  //  }
  ////  fText.keys.reactions += {
  //    case e: KeyTyped => print(e)
  //  }
  //  lText.keys.reactions += {
  //    case e: KeyTyped => print(e)
  //  }
//  val fEdited: EventStream[String] = fText.eventStream({
//    case KeyTyped(_, _, _, _) => fText.text
//  })
  val fEdited = fText.eventStream({
    case EditDone(_) => fText.text
  }).hold(fText.text)
  val lEdited= lText.eventStream({
    case EditDone(_) => lText.text
  }).hold(lText.text)

  val nameEdit = fEdited.flatMap(firstName => lEdited.map(lastName => Name(firstName, lastName)))
  //fEdited.foreach(println)
  //lEdited.foreach(println)
  nameEdit.change.foreach(println)
    cList.bindSelected({
    x => x match {
      case Some(name) => {
        fText.text = name.first
        lText.text = name.last
      }
      case None =>
    }

  },nameEdit.change)
}