package org.identifier

import akka.actor.{ActorSystem, Props}
import org.identifier.api.RestfulApi
import org.identifier.service.IdentifierService

/**
  * Created by Chuan.Chen on 8/03/17.
  */
object SingleIdentifierApp extends App {

  implicit val system = ActorSystem("SingleSystem")
  val deci= system.actorOf(Props[IdentifierService])

  system.actorOf(Props(new RestfulApi(deci, 8888)))

}
