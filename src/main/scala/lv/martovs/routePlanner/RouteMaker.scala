package lv.martovs.routePlanner

class RouteMaker(
                  config: RouteConfig,
                  pds: PointDistanceService,
                  mutate: Seq[RoutePoint] => Seq[RoutePoint],
                  metric: Metric[Double]
                ) {
  assert(config.lockedStartPointIds.forall(id => config.points.exists(rp => rp.id == id)), "Locked point missing in point list")
  assert(config.lockedFinishPointIds.forall(id => config.points.exists(rp => rp.id == id)), "Locked point missing in point list")

  // validate all points in config locked are listed in points section

  def resolvePointById(pId: String): RoutePoint = config.points.find(point => point.id == pId).get

  val startSeq: Seq[RoutePoint] = config.lockedStartPointIds.map(resolvePointById)
  val finishSeq: Seq[RoutePoint] = config.lockedFinishPointIds.map(resolvePointById)
  val middlePoints: Set[RoutePoint] = config.points
    .filterNot(p => config.lockedStartPointIds.contains(p.id) || config.lockedFinishPointIds.contains(p.id))

  val middleSeq: Seq[RoutePoint] = scala.util.Random.shuffle(middlePoints).toSeq
  println(middleSeq)

  var checkpoint: Seq[RoutePoint] = middleSeq
  var score: Double = metric.max
  for (i <- 0 to 100) {
    val candidate = mutate(checkpoint)
    val eval = metric.eval(pds, startSeq ++ candidate ++ finishSeq)
    if (eval < score) {
      score = eval
      checkpoint = candidate
      println(s"Improved to ${score} at step ${i}")
    }
  }
  var i = 0
  while (score > config.timeLimitSeconds & i < 4) {

    checkpoint = dropHeaviest(checkpoint)
    val eval = metric.eval(pds, (startSeq ++ checkpoint ++ finishSeq).toList)
    score = eval
    println(s"Improved to ${score} at drop ${i}")
    i += 1
  }

  println("Fin")


  def dropHeaviest(points: Seq[RoutePoint]): Seq[RoutePoint] = {
    val maxWeightIdx = getDropWeights(points).zipWithIndex.maxBy(_._1)._2
    points.slice(0, maxWeightIdx) ++ points.slice(maxWeightIdx + 1, points.size)
  }

  def getDropWeights(points: Seq[RoutePoint]): Seq[Double] = {
    points.slice(0, points.size - 2) lazyZip points.slice(1, points.size - 1) lazyZip points.slice(2, points.size) map {
      case triplet => pds.distanceBetween(triplet._1, triplet._2) + pds.distanceBetween(triplet._2, triplet._3)
    }
  }
}
