package lv.martovs.routePlanner.distanceService
import lv.martovs.routePlanner.RoutePoint

trait DistanceService {
  def distanceBetween(p1: RoutePoint, p2: RoutePoint): Double
}
