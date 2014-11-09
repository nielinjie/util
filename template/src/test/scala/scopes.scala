package none.domain

import java.util.UUID

import com.escalatesoft.subcut.inject.NewBindingModule
import com.github.athieriot.CleanAfterExample
import name.nielinjie.common.baidu.auth.{BaiduAuth, Author}
import name.nielinjie.common.domain.{UsersRepositoryId, Users, User}
import name.nielinjie.common.plan.{UserInfoPlan, PassPlan, WelcomePlan, AllPlan}
import name.nielinjie.common.repository.{MongoRepository, MongoConfig, Mongo, Repository}
import org.specs2.specification.Scope

import org.json4s.JsonDSL._
import unfiltered.filter.Plan
import none.common.VendorIdAuthor
import none.config.BindingKeys.AllPlanId
import none.plan.DatasPlan


trait DomainConfig {

  object EmbedMongo extends MongoConfig("localhost", 12345, "none", None)

  class Config extends NewBindingModule(implicit module => {
    import module._
    bind[MongoConfig] toProvider EmbedMongo
    bind[Mongo] toModuleSingle { _ => new Mongo}
    bind[Repository] idBy DatasRepositoryId toModuleSingle { _ => new MongoRepository("datas")}
    bind[Datas] toModuleSingle { _ => new Datas}


  })

  val config = new Config
  val datas = config.inject[Datas](None)
}

trait UsersConfig extends DomainConfig {
  val a = User(UUID.randomUUID(), "a", "a")
  val b = User(UUID.randomUUID(), "b", "b")
  val c = User(UUID.randomUUID(), "c", "c")
}


trait PlanConfig {

  object EmbedMongo extends MongoConfig("localhost", 12345, "none", None)

  class Config extends NewBindingModule(implicit module => {
    import module._

    bind[Author] toModuleSingle {_ =>new VendorIdAuthor}

    bind[MongoConfig] toProvider EmbedMongo
    bind[Mongo] toModuleSingle { _ => new Mongo}
    bind[Repository] idBy DatasRepositoryId toModuleSingle { _ => new MongoRepository("datas")}
    bind[Repository] idBy UsersRepositoryId toModuleSingle (_ => new MongoRepository("users"))

    bind[Datas] toModuleSingle { _ => new Datas}
    bind[Users] toModuleSingle { _ => new Users}


    bind[List[Plan]] toModuleSingle {
      implicit m =>
        List(
          //        welcome and pass plan works only in container
          //          new WelcomePlan("./public/front.html"),
          //          new PassPlan
          new DatasPlan,
          new UserInfoPlan
        )
    }

    bind[Plan] idBy AllPlanId toModuleSingle {
      implicit m =>
        new AllPlan()
    }

  })

  val config = new Config

  val allPlan: Plan = try {
    config.inject[Plan](Some(AllPlanId))
  } catch {
    case e =>
      e.printStackTrace()
      null
  }
}
