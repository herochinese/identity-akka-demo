import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.ddata.Replicator.Get
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.event.NoLogging
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.identifier.IdentifierApp
import org.identifier.api.{RestfulApi, RestfulApiRoutes}
import org.identifier.domain.Messages.IdentifierResponse
import org.identifier.service.IdentifierService
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

/**
  * Created by Chuan.Chen on 5/01/17.
  */
class ApiTestCases extends WordSpec with Matchers with ScalatestRouteTest  with BeforeAndAfterEach with RestfulApiRoutes {

  val config = ConfigFactory.load()
  val uuid = config.getString("application.uuid")
  val httpPort = config.getInt("application.http.port")
  val generatorName = s"generator_$uuid"

  ClusterSharding(system).start(
    typeName = IdentifierService.name,
    entityProps = IdentifierService.props,
    settings = ClusterShardingSettings(system),
    extractShardId = IdentifierService.extractShardId,
    extractEntityId = IdentifierService.extractEntityId
  )

  override implicit val decider: ActorRef = ClusterSharding(system).shardRegion(IdentifierService.name)

  override def beforeEach(): Unit = {


  }

  "The path '/identifier/test' " should {
    "return json for GET request" in {
      Get("/identifier/test") ~>routes~> check {
        responseAs[String] shouldEqual "{\"Result\":\"This is just test! For Job! Yeah?\"}"
      }

    }
  }

  "The path '/identifier/id' " should {
    "return identifier as json for GET request" in {
      Get("/identifier/id") ~>routes~> check {
        status.isFailure() shouldEqual true
      }

    }
  }

}
