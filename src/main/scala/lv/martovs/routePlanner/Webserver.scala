package lv.martovs.routePlanner

import cats.effect.{ExitCode, IO, IOApp}
import io.circe.syntax.EncoderOps
import lv.martovs.routePlanner.store.{Task, TaskItemStatus, TaskStore}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext


//final case class RouteConfig2(test: String)


case class ApiPlanResponse(routeId: String)

object Webserver extends IOApp {
  import io.circe.generic.auto._
  import org.http4s.circe.CirceEntityCodec._
  import lv.martovs.routePlanner.store.XX._

  private val routes = HttpRoutes.of[IO] {
    // curl -X POST "localhost:9003/plan" --header "Content-Type: application/json" --data "{\"test\":\"xyz\"}"
    case req@POST -> Root / "api" / "plan" =>
      req.as[RouteConfig].flatMap { cnf =>
        println(cnf)
        val newId = Math.random().toString
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
    case _ => {
      println("Smth else")
      NotFound()
    }

  }

  private[routePlanner] val httpApp = {
    routes
  }.orNotFound


  override def run(args: List[String]): IO[ExitCode] = BlazeServerBuilder[IO](ExecutionContext.global)
    .bindHttp(port = 9003, host = "0.0.0.0")
    .withHttpApp(httpApp)
    .serve
    .compile
    .drain
    .as(ExitCode.Success)
}
