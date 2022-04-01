package com.projet.controller
import com.projet.Main.sc
import akka.http.scaladsl.server.Directives.{complete, _}
import com.projet.model.Message

import java.util.Date
// akka http
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class Analytics(nbmessages: Long, alertsPerHour: Array[(Int,Int)], cartography: Array[(Double,Double)], messagesPerDrone: Array[(Int,Int)])

object AppController {

  val peacescore_threshold = 4
  implicit val AnalyticsFormat: spray.json.RootJsonFormat[Analytics] = jsonFormat4(Analytics)

  def getAnalytics() = {
    val rdd = sc.textFile("../DataLake Consumer/data/test-*.json")
      .map(Message.fromJson(_))

    // nombre de messages reçus
    val nbmessages = rdd.count()

    // nombre d'alertes par heure de la journée
    val alertsPerHour = rdd.flatMap(message => {
      message.surroundingPeople
        .filter(p => p.peaceScore <= peacescore_threshold)
        .map(p => (new Date(message.time).getHours,1))
    })
      .reduceByKey((a,b) => a+b)
      .collect()

    // cartographie des alertes
    val cartography = rdd.filter(message => message.surroundingPeople.filter(p => p.peaceScore <= peacescore_threshold).size > 0)
      .map(message => (message.location.latitude, message.location.longitude))
      .collect()

    // nombre de messages envoyes par drone
    val messagesPerDrone = rdd.map(message => (message.id, 1))
      .reduceByKey((a,b) => a+b)
      .takeOrdered(5)(Ordering[(Int,Int)].reverse.on(x =>(x._2,1)))
    // .foreach(println)


    complete(Analytics(nbmessages,alertsPerHour,cartography,messagesPerDrone))
  }


}