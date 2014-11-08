package name.nielinjie.util.test


import scala.collection.JavaConversions._

import com.mongodb.{ServerAddress, MongoClient}
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.specs2.mutable.SpecificationLike
import org.specs2.specification._
import unfiltered.specs2.Hosted


trait MongoAndUnfiltered extends FragmentsBuilder with Hosted with AfterExample {
  self: SpecificationLike =>

  import unfiltered.jetty._

  def afterServer = {
    server.stop()
    server.destroy()
  }

  def beforeServer = {
    server.start()
  }

  def setup: (Server => Server)

  lazy val server = setup(Server.http(port))


  //  override def sequential: Arguments = args(isolated = false, sequential = true)
  //
  //  override def isolated: Arguments = args(isolated = true, sequential = false)

  //Override this method to personalize testing port
  def embedConnectionPort(): Int = {
    12345
  }

  //Override this method to personalize MongoDB version
  def embedMongoDBVersion(): Version.Main = {
    Version.Main.PRODUCTION
  }

  lazy val network = new Net(embedConnectionPort, Network.localhostIsIPv6)

  lazy val mongodConfig = new MongodConfigBuilder()
    .version(embedMongoDBVersion)
    .net(network)
    .build

  lazy val runtime = MongodStarter.getDefaultInstance

  lazy val mongodExecutable = runtime.prepare(mongodConfig)

  override def map(fs: => Fragments) = startMongo ^ fs ^ stoptMongo

  private def startMongo() = {
    Example("Start Mongo and Server", {
      beforeServer
      mongodExecutable.start
      success
    })
  }

  private def stoptMongo() = {
    Example("Stop Mongo and Server", {
      afterServer
      mongodExecutable.stop
      success
    })
  }


  lazy val mongoClient = new MongoClient(new ServerAddress(network.getServerAddress(), network.getPort()));

  def after() {
    mongoClient.getDatabaseNames().map {
      mongoClient.getDB(_)
    }.foreach {
      _.dropDatabase()
    }
  }
}