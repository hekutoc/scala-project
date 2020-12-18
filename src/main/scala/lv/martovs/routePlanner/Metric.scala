package lv.martovs.routePlanner


sealed trait Metric[S <: Double] {
  def eval(pds: PointDistanceService, points: Seq[RoutePoint]): S
  def evalScore(points: Seq[RoutePoint]): Double
  def max: S

}
object DistanceMetric extends Metric[Double] {

  def eval(pds: PointDistanceService, points: Seq[RoutePoint]): Double = {
    val pairs = pairZip(points)
    val consumption = pairs.map({
      case (p1, p2) => pds.distanceBetween(p1, p2)
    }).sum
    consumption
  }


  private def pairZip[A](list: Seq[A]): Seq[(A, A)] = {
    val length = list.length
    list.slice(0, length - 1) zip list.slice(1, length)
  }

  def evalScore(points: Seq[RoutePoint]): Double = points.map(p => p.score).sum

  def max: Double = Double.MaxValue
}
