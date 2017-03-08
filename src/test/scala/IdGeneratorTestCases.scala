import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.identifier.domain.Messages.{Identifier, IdentifierRequest, IdentifierResponse}
import org.identifier.service.IdGenerator
import org.scalatest.{BeforeAndAfterAll, FunSpecLike, Matchers}

import scala.concurrent.duration._

/**
  * Created by Chuan.Chen on 5/01/17.
  */
class IdGeneratorTestCases extends TestKit(ActorSystem("indentifier")) with ImplicitSender with FunSpecLike
  with Matchers with BeforeAndAfterAll {

  val act = system.actorOf(Props[IdGenerator], "generator_999-99-99")

  override def beforeAll = {

  }

  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  describe("IdGenerator->IdentifierRequest(1)") {
    it("should return IdentifierResponse with 1 identifier & id in idSeq") {

      act ! IdentifierRequest(1)

      receiveWhile(5 seconds) {
        case x:IdentifierResponse =>
          x.identifiers.size should be(1)
          assert( x.identifiers(0).createdTime > 0)
          val js = x.identifiers(0).idSeq.split("\\.")
          assert(js(1).toLong == x.identifiers(0).id)
      }
    }
  }

  describe("IdGenerator->IdentifierRequest(2)") {
    it("should return IdentifierResponse with 2 identifier & different 1") {

      act ! IdentifierRequest(2)

      receiveWhile(5 seconds) {
        case x:IdentifierResponse =>
          x.identifiers.size should be(2)
          assert( x.identifiers(0).createdTime > 0)
          assert( x.identifiers(1).id - x.identifiers(0).id == 1)

      }
    }
  }

}
