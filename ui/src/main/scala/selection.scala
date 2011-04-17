package nielinjie
package util.ui

import collection.mutable.ListBuffer

class Model[A](value_ : A, val bind: Option[Bind[A]]) extends Changable[A] {
  private var value: A = value_

  def set(f: A => A) = {
    val oldValue = this.value
    val newValue = f(this.value)
    this.value = newValue
    this.bind.foreach {
      b =>
        b.push(this.value)
    }
    notifyChangeListener(oldValue, this.value)
  }

  def get = {
    this.bind.foreach {
      b =>
        this.value = b.pull()
    }
    this.value
  }

}

case class Bind[A](val push: (A => Unit), val pull: () => A)

case class Selection[A](var value: Option[A], var index: Option[Int]) {
  def saveToMaster(master: List[A]): List[A] = {
    index.map {
      i =>
        master.updated(i, value.get)
    }.getOrElse(master)
  }

  def selected = !(this.index.isEmpty)
}


class MasterDetail[A](var master: List[A]) {
  def check = {
    println(master)
    println(selection)
  }

  var selection: Selection[A] = new Selection[A](None, None)
  var detailBind: Option[Bind[A]] = None
  var masterBind: Option[Bind[List[A]]] = None

  def selectNone() = {
    //TODO support none selection binding
    this.selection.value = None
    this.selection.index = None
  }


  def select(index: Option[Int]): Unit = {

    val oldSelection = this.selection
    saveDetail
    this.selection.index = index
    this.selection.value = index.map(master.apply(_))
    this.detailBind.foreach {
      b =>
        this.selection.value match {
          case Some(v) =>
            b.push(v)
          case None =>
        }
    }

  }


  def saveDetail = {
    this.detailBind.foreach {
      b =>
        if (this.selection.selected) {
          this.selection.value = Some(b.pull())
        }
    }
    this.master = this.selection.saveToMaster(this.master)
    masterBind.foreach {
      b =>
        b.push(this.master)
    }
  }
}

trait Changable[T] {
  val onChanges: ListBuffer[(T, T) => Unit] = new ListBuffer()

  def notifyChangeListener(oldValue: T, newValue: T): Unit = {
    onChanges.foreach {
      f =>
        f(oldValue, newValue)
    }
  }

  def onChange(f: (T, T) => Unit) = {
    this.onChanges.append(f)
  }

}

import scala.swing._
import reactive._

object SwingSupport extends Observing {
  def bindToListView[A](masterDetail: MasterDetail[A], listView: ListView[A]) = {
    import event.SelectSupport._
    listView.listData = masterDetail.master
    val selectSupport = listView.singleSelect
    masterDetail.masterBind = Some(Bind({
      list => selectSupport.updateKeepingSelect(list)
    }, {
      () => Nil
    }))

    selectSupport.selectIndexChangedEvent.foreach {
      selIndex =>
        println("from event stream  - " + selIndex)
        masterDetail.select(selIndex)
    }
  }
}


