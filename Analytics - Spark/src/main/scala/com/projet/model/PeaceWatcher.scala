package com.projet.model

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read,write}


case class Message(
                    id : Int,
                    location: Map[String, Double],
                    surroundingPeople: List[Map[String, Any]]
                  ){
  def toJson(): String = {
    implicit val formats = Serialization.formats(NoTypeHints)
    write(this)
  }

}

object Message{
  def fromJson(message: String): Message = {
    implicit val formats = Serialization.formats(NoTypeHints)
    read[Message](message)
  }
}