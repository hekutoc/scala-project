package lv.martovs.routePlanner

import cats.effect.{ExitCode, IO, IOApp}
import io.circe.syntax.EncoderOps
import lv.martovs.routePlanner.store.{Task, TaskItemStatus, TaskStore}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import scala.util.Random


case class ApiPlanResponse(routeId: String)

object Webserver extends IOApp {

  import io.circe.generic.auto._
  import org.http4s.circe.CirceEntityCodec._
  import lv.martovs.routePlanner.store.Task._

  private val routes = HttpRoutes.of[IO] {

    case req@POST -> Root / "api" / "plan" =>
      req.as[RouteConfig].flatMap { cnf =>
        val newId = randomAlphaNumericString(10)
        val item = Task.Item(newId, TaskItemStatus.Pending, cnf)
        TaskStore.add(item)
        Runner.notify(TaskStore)
        Ok(ApiPlanResponse(item.id).asJson)
      }

    case GET -> Root / "api" / "route" / routeId => {
      TaskStore.get(routeId) match {
        case None => NotFound()
        case Some(v) => Ok(v.asInstanceOf[Task.Item].asJson)
      }
    }
    case _ =>       NotFound()
  }

  private[routePlanner] val httpApp = {
    routes
  }.orNotFound


  def randomAlphaNumericString(length: Int): String = {
    def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
      val sb = new StringBuilder
      for (i <- 1 to length) {
        val randomNum = Random.nextInt(chars.length)
        sb.append(chars(randomNum))
      }
      sb.toString
    }


    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }


  override def run(args: List[String]): IO[ExitCode] = BlazeServerBuilder[IO](ExecutionContext.global)
    .bindHttp(port = 9003, host = "0.0.0.0")
    .withHttpApp(httpApp)
    .serve
    .compile
    .drain
    .as(ExitCode.Success)
}
