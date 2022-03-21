package com.projet

import com.projet.model.{Message, PeaceWatcher}

import java.util.Properties
import org.apache.kafka.clients.producer._

object Main {

  def main(args: Array[String]): Unit = {

    val  props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")

    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val topic="quickstart-events"

    val producer = new KafkaProducer[String, String](props)

    PeaceWatcher(producer).sendMessage()

    producer.close()
  }

}
