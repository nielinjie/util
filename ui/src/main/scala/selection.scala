package nielinjie
package util.ui

import collection.mutable.ListBuffer

case class Model[A](value_ : A) extends Changable[A] {
  private var value: A = value_

  def set(f: A => A) = {
    val oldValue = this.value
    this.value = f(this.value)
    notifyChangeListener(oldValue, this.value)
  }

  def get = {
    this.value
  }
}

class View[A]

class MV[A](val model: Model[A], val view: View[A]) {

}

case class Selected[A](val value: Model[A], val index: Int)

class MasterDetail[A](val master: Model[List[A]]) extends Changable[Option[Selected[A]]] {
  var selected: Option[Selected[A]] = None

  def select(value: A): Unit = {
    val index = this.master.get.indexOf(value)
    if (index != -1) select(index)
  }

  def select(index: Int): Unit = {
    val oldSelected = this.selected
    this.selected.foreach({
      sel =>
        saveDetail
    })
    this.selected = Some(Selected(Model(master.get.apply(index)), index))
    notifyChangeListener(oldSelected, this.selected)
  }

  def onSaveDetail(value: A) = {
    this.selected.foreach {
      sel =>
        master.set({
          oldValue: List[A] =>
            oldValue.updated(sel.index, value)
        })
    }
  }

  def saveDetail = {
    this.selected.foreach(
      sel =>
        onSaveDetail(sel.value.get)
    )

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


