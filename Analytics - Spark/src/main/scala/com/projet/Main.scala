package com.projet
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.projet.model.Message
import org.apache.spark._
import com.projet.router.router

import java.util.Date
import scala.io.StdIn

object Main {

  def server: Unit = {

    implicit val actorSystem = ActorSystem(Behaviors.empty, "akka-http")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = actorSystem.executionContext
    val bindFuture = Http().newServerAt("0.0.0.0", 9001).bind(router.init)
  }

  def main(args: Array[String]): Unit = {
    server
    println(s"Server now online. Please navigate to http://localhost:9001")
    while (true) Thread.sleep(1)
  }

}