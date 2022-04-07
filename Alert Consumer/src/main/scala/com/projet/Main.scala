package com.projet
import com.projet.Main.batchTime
import com.projet.model.{Message, Person}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf

import com.projet.model.Mail._




object Main {

  val resource_dir = "src/main/resources/"
  val batchTime = 5

  def main(args: Array[String]): Unit = {

    val server = if(args.size != 0) args(0) else "localhost"
    val topic=Array("quickstart-events")

    val sparkConf = new SparkConf()
      .setAppName("spark-app")
      .setMaster("local[*]")
      .set("spark.driver.host","127.0.0.1")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> s"$server:9092",
      "key.deserializer"-> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "group.id" -> "alert",
      /*"auto.offset.reset" -> "latest",
      "enable.auto.commit" -> false*/
    )

    val finalBatchTime = if(args.size > 1) args(1).toInt else batchTime
    val ssc = new StreamingContext(sparkConf, Seconds(finalBatchTime))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String,String](topic,kafkaParams)
    ).map(record => Message.fromJson(record.value()))
      .map(record => {
        val people = record.surroundingPeople.filter(e => e.peaceScore <= 4)
        val alerts = people.map(e => toAlertMessage(record,e))
        alert(record,alerts)
        record
      })
      .print()

    ssc.start()
    ssc.awaitTermination()

  }


  def toAlertMessage(record: com.projet.model.Message, e: Person) = {

      s"""ALERT!! L'habitant ${e.nom} a un peace score = ${e.peaceScore}
         |Coordonnées GPS: (${record.location.latitude},${record.location.longitude})
         |""".stripMargin

  }

  def alert(record: Message, alerts: List[String]) = {

    if(alerts.isEmpty){};
    else {
      send a new Mail (
        from = ("bde.iui.g5@gmail.com", "PeaceLand"),
        to = "bde.iui.g5@gmail.com",
        subject = "PeaceLand Alert",
        message = alerts.mkString("\n")
      )
    }

  }


}
