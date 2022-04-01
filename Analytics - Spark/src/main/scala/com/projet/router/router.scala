package com.projet.router

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import com.projet.controller.AppController

object router {

  def init = {
    val getCountry = get {
        path("api" / "analytics"){AppController.getAnalytics}
    }

    val route = cors() {
      concat(getCountry)
    }

    route
  }
}
