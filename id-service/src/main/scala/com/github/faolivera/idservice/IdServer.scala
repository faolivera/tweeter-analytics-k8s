package com.github.faolivera.idservice

import java.time.Clock
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.UseHttp2.Always
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

object IdServer {

  def main(args: Array[String]): Unit = {
    // important to enable HTTP/2 in ActorSystem's config
    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system: ActorSystem = ActorSystem("IdServer", conf)
    new IdServer(system).run()
  }
}

class IdServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys = system
    implicit val ec: ExecutionContext = sys.dispatcher
    implicit val mat: Materializer = ActorMaterializer()

    val config = IdServiceImpl.Configuration(
      machineBits = 10,
      sequentialBits = 6,
      machineId = 1
    )

    val clock = Clock.systemUTC()

    val service: HttpRequest => Future[HttpResponse] =
      IdServiceHandler(new IdServiceImpl(config, clock))

    val bound = Http().bindAndHandleAsync(
      service,
      interface = "0.0.0.0",
      port = 8080,
      connectionContext = HttpConnectionContext(http2 = Always)
    )

    bound.foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }

    bound
  }
}
