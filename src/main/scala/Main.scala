
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.Await

import java.io.File

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("Web")
    import system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val routes = 
      get(
        concat(
          pathSingleSlash(getFromFile(new File("out/index.html"))),
          path("public" / Remaining)(path => getFromFile(new File("out/public/" + path)))
        )
      )

    Await.result(Http().bindAndHandle(routes, "0.0.0.0", 8080), 1.seconds)
    Await.result(system.whenTerminated, Duration.Inf)
  }
}