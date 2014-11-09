package none.server

import java.util.Properties

import org.slf4j.LoggerFactory
import none.config.BindingKeys.AllPlanId
import none.config.{IntegrateConfiguration, ProductConfiguration}
import unfiltered.filter.Plan
import unfiltered.filter.Plan.Intent



class JettyFilter extends unfiltered.filter.Plan {
  val logger = LoggerFactory.getLogger(classOf[JettyFilter])

  //  implicit val bindingModule =  IntegrateConfiguration
  val configs=new Properties()
  configs.load(classOf[JettyFilter].getClassLoader.getResourceAsStream("env.properties"))
  val con=configs.getProperty("env","integer")
  logger.info(s"evn = ${con}")
  implicit val bindingModule =  con match {
    case "integerM" => IntegrateConfiguration
    case "product" => ProductConfiguration
  }
  override def intent: Intent =      bindingModule.inject[Plan](Some(AllPlanId)).intent
}
