package org.identifier.api

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.pattern.ask
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, logRequestResult, path, pathPrefix}
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import org.identifier.domain.Messages._

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._
import spray.json._
import akka.http.scaladsl.server.Directives._

/**
  * Created by Chuan.Chen on 5/01/17.
  */
trait RestfulApiRoutes extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  implicit val decider: ActorRef

  implicit val timeout = Timeout(5 seconds)



  val routes = {
    logRequestResult("api-server.identifier") {
      //BO:
      pathPrefix("identifier") {
        (get & path(Segment)) {
          cmd =>
            complete {
              cmd match {
                case "test" => """{"Result":"This is just test! For Job! Yeah?"}"""

                case "mem" =>
                  val f = decider.ask(IdentifiersMemory(0, 0)).mapTo[IdentifiersMemory]
                  val result = Await.result(f, 10 seconds)
                  result.toJson.toString()

                case "id" =>
                  val f = decider.ask(IdentifierRequest(1)).mapTo[IdentifierResponse]
                  val result = Await.result(f, 5 seconds)
                  result.toJson.toString()
              }
            }
        }
      }
      //EO:
    } ~ logRequestResult("api-server.identifiers") {
      pathPrefix("identifiers") {
        (get & path(Segment)) {
          num =>
            complete {
              num match {
                case x =>  {
                  try {

                    val f = decider.ask(IdentifierRequest(x.toInt)).mapTo[IdentifierResponse]
                    val result = Await.result(f, 5 seconds)
                    result.toJson.toString()
                  } catch {
                    case e: Exception => "Error number exception!"
                  }
                }
              }
            }
        }
      }
    }

  }

}

class RestfulApi(deci: ActorRef, port:Int) extends Actor with RestfulApiRoutes with ActorLogging {


  override implicit val system = context.system
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()
  override implicit val decider = deci

  override def receive: Receive = {
    case _ =>
  }

  Http().bindAndHandle(routes, "0.0.0.0", port)

}


