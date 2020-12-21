package lv.martovs.routePlanner.distanceService

import io.circe.generic.JsonCodec
import io.circe.parser.decode
import scalaj.http.Http

object OSRM_API {

  @JsonCodec final case class Scoreboard(durations: List[List[Double]], code: String)

  case class Point(lat: Double, lon: Double) {
    override def toString: String = Seq(lon, lat).mkString(",")
  }


  def getMatrix(points: Seq[Point]): Option[List[List[Double]]] = {
    val url = s"http://router.project-osrm.org/table/v1/driving/${points.map(_.toString).mkString(";")}"
    println(s"Quering ${url}")
    val body = Http(url).asString.body
    decode[Scoreboard](body).map(resp => resp.durations).toOption
  }
}

object Test extends App{
  val r = OSRM_API.getMatrix(Seq(
    OSRM_API.Point(56.8, 24.23),
    OSRM_API.Point(56.844, 24.22),
    OSRM_API.Point(56.8123, 24.112),
    OSRM_API.Point(56.8144, 24.12),
  ))
  println(r)
}
