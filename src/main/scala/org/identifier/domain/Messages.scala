package org.identifier.domain

import spray.json.DefaultJsonProtocol


/**
  * Created by Chuan.Chen on 5/01/17.
  */
object Messages {

  case class IdentifierRequest(num:Int = 1)
  case class IdentifierResponse(identifiers: Array[Identifier])
  case class Identifier(id:Long, idSeq:String, createdTime:Long)
  case class Identifier2Saving(id:Long, createdTime:Long)

  case class IdentifiersMemory(num:Int, memorySize:Long)


  trait Protocols extends DefaultJsonProtocol {
    implicit val identifierFormat = jsonFormat3(Identifier.apply)
    implicit val identifierRequestFormat = jsonFormat1(IdentifierRequest.apply)
    implicit val identifierResponseFormat = jsonFormat1(IdentifierResponse.apply)

    implicit val identifiersMemoryFormat = jsonFormat2(IdentifiersMemory.apply)
  }

}
