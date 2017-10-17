import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Success

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val executionService: ExecutionContextExecutor = system.dispatcher

  val uri = "http://media-sov.musicradio.com:80/Arrow"

  Http().singleRequest(HttpRequest(uri = uri))
    .onComplete {
      case Success(response@HttpResponse(StatusCodes.OK, _, entity, _)) =>
        println("Open connection")
        entity.dataBytes.idleTimeout(2.seconds) // This stream will never stop
          .watchTermination() {
          (_, futDone) => futDone.onComplete(_ => println("complete"))
        }
    }

  StdIn.readLine()
  system.terminate()

}
