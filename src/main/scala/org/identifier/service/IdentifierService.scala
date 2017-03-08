package org.identifier.service

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import com.typesafe.config.ConfigFactory
import org.identifier.domain.Messages.{IdentifierRequest, IdentifiersMemory}


/**
  * Created by Chuan.Chen on 5/01/17.
  */

object IdentifierService {

  val numberOfShards = 3


  def name = "IdentifierService"
  def props = Props[IdentifierService]

  def extractShardId:ExtractShardId = {

    case IdentifierRequest(num)=>
      (num % numberOfShards).toString

    case  IdentifiersMemory(num, _)=>
      (num % numberOfShards).toString
  }

  def extractEntityId: ExtractEntityId = {
    case msg@IdentifierRequest(num) =>
      (num.toString, msg)

    case msg@IdentifiersMemory(num, _) =>
      (num.toString, msg)

  }
}


class IdentifierService extends Actor {

  val config = ConfigFactory.load()
  val uuid = config.getString("application.uuid")
  val numberOfActors = config.getInt("application.number-of-actor")
  //val generatorPath = s"/user/generator_$uuid"
  //val actor = context.actorSelection(generatorPath)

  override def receive: Receive = {

    case identifierRequest: IdentifierRequest =>
      val name = s"generator_${identifierRequest.num%numberOfActors}"
      val actor = context.child(name) getOrElse context.actorOf(Props[IdGenerator], name)
      Thread.sleep(2)
      actor.tell(identifierRequest, sender)

    case identifiersMemory:IdentifiersMemory =>
      val actor = context.child("generator_mem") getOrElse context.actorOf(Props[IdGenerator], "generator_mem")
      actor.tell(identifiersMemory, sender)

  }
}
