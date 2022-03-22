package com.projet

import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf
import scala.util.Random


object Main {

  val resource_dir = "data"
  val timeout = 10

  def main(args: Array[String]): Unit = {

    val topic=Array("quickstart-events")

    val sparkConf = new SparkConf()
      .setAppName("spark-app")
      .setMaster("local[*]")
      .set("spark.driver.host","127.0.0.1")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer"-> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "group.id" -> "datalake",
      /*"auto.offset.reset" -> "latest",
      "enable.auto.commit" -> false*/
    )

    val ssc = new StreamingContext(sparkConf, Seconds(timeout))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String,String](topic,kafkaParams)
    ).map(_.value)
      .foreachRDD(rdd => {
        if(!rdd.isEmpty()) rdd.saveAsTextFile(s"${resource_dir}/test-${Random.alphanumeric.take(10).mkString}.json")
      })

    ssc.start()
    ssc.awaitTermination()

  }

}
