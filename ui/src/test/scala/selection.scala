package nielinjie
package util.ui

import org.specs2.mutable._

object SelectionSpec extends Specification {
//  "master detail just work" in {
//    val masterList = List("1", "2", "3", "4", "5")
//    val masterDetail = new MasterDetail(Model(masterList))
//    masterDetail.selected must equalTo(None)
//    masterDetail.select(0)
//    masterDetail.selected.map {
//      _.value.get
//    } must equalTo(Some("1"))
//    masterDetail.select(1)
//    masterDetail.selected.map {
//      _.value.get
//    } must equalTo(Some("2"))
//    masterDetail.master.get must equalTo(List("1", "2", "3", "4", "5"))
//    masterDetail.selected.foreach {
//      _.value.set({
//        x => "8"
//      })
//    }
//    //will not save detail's change to master
//    masterDetail.master.get must equalTo(List("1", "2", "3", "4", "5"))
//    masterDetail.select(3)
//    //change master when pointer move
//    masterDetail.master.get must equalTo(List("1", "8", "3", "4", "5"))
//    masterDetail.selected.foreach{
//      _.value.set({
//        x=> "9"
//      })
//    }
//    masterDetail.master.get must equalTo(List("1", "8", "3", "4", "5"))
//    masterDetail.saveDetail
//    masterDetail.master.get must equalTo(List("1", "8", "3", "9", "5"))
//  }

}