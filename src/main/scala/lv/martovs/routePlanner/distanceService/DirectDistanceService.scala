package lv.martovs.routePlanner.distanceService

import lv.martovs.routePlanner.RoutePoint

class DirectDistanceService extends DistanceService {
  override def distanceBetween(p1: RoutePoint, p2: RoutePoint): Double = {
    val lon1: Double = Math.toRadians(p1.longitude);
    val lon2: Double = Math.toRadians(p2.longitude);
    val lat1: Double = Math.toRadians(p1.latitude);
    val lat2: Double = Math.toRadians(p2.latitude);

    // Haversine formula
    val dlon: Double = lon2 - lon1;
    val dlat: Double = lat2 - lat1;
    val a: Double = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

    val c: Double = 2 * Math.asin(Math.sqrt(a));

    // Radius of earth in kilometers. Use 3956
    // for miles
    val r: Double = 6371;

    // calculate the result
    c * r
  }
}
