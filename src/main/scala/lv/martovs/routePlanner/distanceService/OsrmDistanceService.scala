package lv.martovs.routePlanner.distanceService

import OsrmApi.{Point, getMatrix}
import lv.martovs.routePlanner.RoutePoint

class OsrmDistanceService private(matrix: List[List[Double]], pointsOrdered: Seq[RoutePoint]) extends DistanceService {
  override def distanceBetween(p1: RoutePoint, p2: RoutePoint): Double = {
    val idx1 = pointsOrdered.indexOf(p1)
    val idx2 = pointsOrdered.indexOf(p2)
    matrix(idx1)(idx2)
  }
}

object OsrmDistanceService {
  def initForPoints(points: Set[RoutePoint]): Option[OsrmDistanceService] = {
    val pointsOrdered: Seq[RoutePoint] = points.toSeq
    val matrix: Option[List[List[Double]]] = getMatrix(pointsOrdered.map(p => Point(p.latitude, p.longitude)))
    matrix.map(m => new OsrmDistanceService(m, pointsOrdered))
  }
}