package name.nielinjie.common.plan

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import org.slf4j.Logger
import unfiltered.filter.{Plan, Planify}
import unfiltered.kit.GZip
import unfiltered.response.{InternalServerError, ResponseFunction, ResponseString}

import scala.util.control.Exception._

class AllPlan(implicit val bindingModule:BindingModule) extends Plan with Injectable{
  val plans:List[Plan]=inject[List[Plan]]
  def intent = GZip( plans.reduce({
    (planA:Plan,planB:Plan)=>
      Planify(planA.intent.orElse(planB.intent))
  }).intent)
}
trait PlanHelper{
  self: {def logger:Logger}=>
  def dealAllException(body: => ResponseFunction[Any]): ResponseFunction[Any]   ={
    allCatch.either {
      body
    }.left.map {
      l => {
        logger.error("err in plan",l)
        InternalServerError ~> ResponseString(l.getMessage)
      }
    }.merge
  }
}
