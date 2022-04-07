package com.projet.model

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{write,read}
import org.json4s._
import scala.util.Random
import java.time.Instant


case class PeaceWatcher(kafkaProducer: KafkaProducer[String,String]){
  def sendMessage = () => {

    val topic="quickstart-events"

    (0 to 1000).foreach((i) => {
      val id = Random.nextInt(100)
      val lat = Random.nextFloat*30
      val lon = Random.nextFloat*30
      val nb_people = Random.nextInt(5)+1
      val people: List[Person] = (0 to nb_people).map(i => Person(Random.alphanumeric.take(10).mkString, Math.min(10,Random.nextInt(10)+3.5))).toList
      val time = System.currentTimeMillis()

      val message = Message(id,Coords(lat,lon),people,time)
      val record = new ProducerRecord(topic, "key", message.toJson())
      println(message.toJson())
      kafkaProducer.send(record)
      Thread.sleep(2000)
    })
  }
}

case class Coords(latitude: Double, longitude: Double)
case class Person(nom: String, peaceScore: Double)
case class Message(
   id : Int,
   location: Coords,
   surroundingPeople: List[Person],
   time: Long
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

