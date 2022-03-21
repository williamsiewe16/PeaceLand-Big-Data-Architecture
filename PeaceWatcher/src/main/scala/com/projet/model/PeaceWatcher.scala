package com.projet.model

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write,read}
import org.json4s._


case class PeaceWatcher(kafkaProducer: KafkaProducer[String,String]){
  def sendMessage = () => {

    val messages = List(
      Message(0,Coords(12,10),List[Person](
        Person("tom",5),
        Person("tom",3)
      )),
      Message(0,Coords(12,10),List[Person](
        Person("tom",2),
        Person("tom",7)
      )),
      Message(0,Coords(12,10),List[Person](
        Person("tom",8),
      )),
    )


    val topic="quickstart-events"

    messages.foreach((el) => {
      val record = new ProducerRecord(topic, "key", el.toJson())
      kafkaProducer.send(record)
    })
  }
}

case class Coords(latitude: Double, longitude: Double)
case class Person(nom: String, peaceScore: Double)
case class Message(
   id : Int,
   location: Coords,
   surroundingPeople: List[Person]
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

