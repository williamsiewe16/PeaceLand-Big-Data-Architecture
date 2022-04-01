package com.projet
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.projet.model.Message
import org.apache.spark._
import com.projet.router.router

import java.util.Date

object Main {

  val conf = new SparkConf()
    .setAppName("Spark Analytics")
    .setMaster("local[*]")
    .set("spark.driver.host","127.0.0.1")

  val sc = SparkContext.getOrCreate(conf)

  def server: Unit = {

    implicit val actorSystem = ActorSystem(Behaviors.empty, "akka-http")
    val bindFuture = Http().newServerAt("127.0.0.1", 9001).bind(router.init)
  }

  def main(args: Array[String]): Unit = {
    server
    println(s"Server now online. Please navigate to http://localhost:9001")
  }

}