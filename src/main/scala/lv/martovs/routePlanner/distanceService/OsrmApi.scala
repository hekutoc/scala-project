package lv.martovs.routePlanner.distanceService

import io.circe.generic.JsonCodec
import io.circe.parser.decode
import lv.martovs.routePlanner.config.OsrmApiUrl
import scalaj.http.Http

object OsrmApi {
  @JsonCodec final case class Scoreboard(durations: List[List[Double]], code: String)

  case class Point(lat: Double, lon: Double) {
    override def toString: String = Seq(lon, lat).mkString(",")
  }

  def getMatrix(points: Seq[Point]): Option[List[List[Double]]] = {
    val url = s"${OsrmApiUrl}/table/v1/driving/${points.map(_.toString).mkString(";")}"
    val body = Http(url).asString.body
    decode[Scoreboard](body).map(resp => resp.durations).toOption
  }
}
