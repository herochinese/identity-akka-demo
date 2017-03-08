package org.identifier

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.typesafe.config.ConfigFactory
import org.identifier.api.RestfulApi
import org.identifier.service.{IdGenerator, IdentifierService}

/**
  * Created by Chuan.Chen on 5/01/17.
  */
object IdentifierApp extends App {
  val config = ConfigFactory.load()
  implicit val system = ActorSystem(config.getString("application.name"), config)
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

  val decider = ClusterSharding(system).shardRegion(IdentifierService.name)
  system.actorOf(Props(new RestfulApi(decider, httpPort)))
  system.actorOf(Props[IdGenerator], generatorName)
}
