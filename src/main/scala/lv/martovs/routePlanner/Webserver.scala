package lv.martovs.routePlanner

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext


final case class RouteConfig2(test: String)

object Webserver extends IOApp {
  import io.circe.generic.auto._
  import org.http4s.circe.CirceEntityCodec._


  private val routes = HttpRoutes.of[IO] {
    // curl -X POST "localhost:9003/plan" --header "Content-Type: application/json" --data "{\"test\":\"xyz\"}"
    case req@POST -> Root / "plan" =>
      req.as[RouteConfig2].flatMap { cnf =>
        Ok("started" + cnf.test)
      }


    case GET -> Root / "route" / routeId =>Ok("")
  }

  private[routePlanner] val httpApp = {
    routes
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = BlazeServerBuilder[IO](ExecutionContext.global)
    .bindHttp(port = 9003, host = "localhost")
    .withHttpApp(httpApp)
    .serve
    .compile
    .drain
    .as(ExitCode.Success)
}
