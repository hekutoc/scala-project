package lv.martovs.routePlanner.distanceService

import OSRM_API.{Point, getMatrix}
import lv.martovs.routePlanner.RoutePoint

class OSRMDistanceService(points: Set[RoutePoint]) extends DistanceService {
  val pointsOrdered: Seq[RoutePoint] = points.toSeq
  val matrix: Option[List[List[Double]]] = getMatrix(pointsOrdered.map(p => Point(p.latitude, p.longitude)))

  override def distanceBetween(p1: RoutePoint, p2: RoutePoint): Double = {
    val idx1 = pointsOrdered.indexOf(p1)
    val idx2 = pointsOrdered.indexOf(p2)
    matrix.get(idx1)(idx2)
  }
}
