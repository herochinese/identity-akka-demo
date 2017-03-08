package org.identifier.service

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.typesafe.config.ConfigFactory
import org.identifier.domain.Messages._

import scala.collection.mutable.ArrayBuffer
import akka.actor._
import org.github.jamm.MemoryMeter

/**
  * Created by Chuan.Chen on 5/01/17.
  */
class IdGenerator extends PersistentActor with ActorLogging {
  val config = ConfigFactory.load()
  val uuid = config.getString("application.uuid")
  var idsMemory = new IdsMemory()

  override def persistenceId: String = s"Id-Generator-${uuid}"

  def updateMem(identifier2Saving:Identifier2Saving):Unit ={
    idsMemory = idsMemory.updated(identifier2Saving)
  }

  def numIds = {
    idsMemory.ids.size
  }

  def nextId = {
    idsMemory.id
  }

  override def receiveRecover: Receive = {

    case identifier2Saving:Identifier2Saving =>
      log.info(s"######################BO:receiveRecover->############## ")
        updateMem(identifier2Saving)
      log.info(s"######################EO:receiveRecover->############## ")

    case RecoveryCompleted =>
      deleteMessages(lastSequenceNr-1)

  }

  override def receiveCommand: Receive = {

    case identifierRequest:IdentifierRequest =>
      val identifierResponse =IdentifierResponse(generateIds(identifierRequest.num))
      sender ! identifierResponse

    case mem:IdentifiersMemory =>
      val meter = new MemoryMeter();
      val m = meter.measureDeep(this.idsMemory.ids)
      log.info(s"who am i?  -> ${self.path}")
      log.info(s"measuring idsMemory.ids -> $m")
      log.info(s"total memory -> ${Runtime.getRuntime.totalMemory()}")
      sender ! IdentifiersMemory(this.idsMemory.ids.size, m)

  }


  def generateId(id:Long):Identifier = {

    val createdTime = System.currentTimeMillis()
    val identifier2Saving = Identifier2Saving(id, createdTime)

    persistAsync(identifier2Saving) { identifier2Saving=>
      log.info(s"########## persist(identifier) -> ${identifier2Saving.id}#######")
      updateMem(identifier2Saving)
      //context.system.eventStream.publish(identifier)
    }

    Identifier(id, id2Identifier(id), createdTime)
  }


  def id2Identifier(id:Long):String = {

    s"$uuid.${id}"
  }

  def generateIds(num:Int):Array[Identifier] = {
    val ab = ArrayBuffer.empty[Identifier]
    val n = if (num > 1) num else 1
    var id = nextId
    for(i <- 0 until n) {
      val identifier = generateId(id)
      ab += identifier
      id += 1
    }
    ab.toArray
  }


}


case class IdsMemory( ) {
  var nextId = 0L
  var ids = ArrayBuffer.empty[(Int, Int)]
  def updated(identifier2Saving:Identifier2Saving):IdsMemory = {

    //val x = copy((identifier2Saving.id, identifier2Saving.createdTime)+ ids)
    ids.append((identifier2Saving.id.toInt, identifier2Saving.createdTime.toInt))

    this.nextId = identifier2Saving.id+1
    this
  }
  //def size = ids.size
  def id = nextId
}

