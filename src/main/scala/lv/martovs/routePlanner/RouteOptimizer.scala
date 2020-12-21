package lv.martovs.routePlanner

import lv.martovs.routePlanner.distanceService.DistanceService
import lv.martovs.routePlanner.metrics.Metric

object RouteOptimizer {

  case class OptimalRoute(
                           seq: Seq[RoutePoint],
                           timeToDrive: Long,
                           totalScore: Long
                         )


  def optimize(
                config: RouteConfig,
                pds: DistanceService,
                mutate: Seq[RoutePoint] => Seq[RoutePoint],
                metric: Metric[Double]
              ): OptimalRoute = {
    assert(config.lockedStartPointIds.forall(id => config.points.exists(rp => rp.id == id)), "Locked point missing in point list")
    assert(config.lockedFinishPointIds.forall(id => config.points.exists(rp => rp.id == id)), "Locked point missing in point list")

    // validate all points in config locked are listed in points section

    def resolvePointById(pId: String): RoutePoint = config.points.find(point => point.id == pId).get

    val startSeq: Seq[RoutePoint] = config.lockedStartPointIds.map(resolvePointById)
    val finishSeq: Seq[RoutePoint] = config.lockedFinishPointIds.map(resolvePointById)
    val middlePoints: Set[RoutePoint] = config.points
      .filterNot(p => config.lockedStartPointIds.contains(p.id) || config.lockedFinishPointIds.contains(p.id))

    val middleSeq: Seq[RoutePoint] = scala.util.Random.shuffle(middlePoints).toSeq

    var checkpoint: Seq[RoutePoint] = middleSeq
    var score: Double = metric.max

    var mutationsLeft = config.mutationCount
    var totalSteps = 0
    while (mutationsLeft > 0) {
      mutationsLeft -= 1
      totalSteps += 1
      val candidate = mutate(checkpoint)
      val eval = metric.eval(pds, startSeq ++ candidate ++ finishSeq)
      if (eval < score) {
        score = eval
        checkpoint = candidate
        mutationsLeft = config.mutationCount
      }
    }

    def timePrint(timeInSeconds: Long): String = {
      val hrs: Long = timeInSeconds / 60 / 60
      val min: Long = timeInSeconds / 60 % 60
      val sec: Long = timeInSeconds % 60
      f"$hrs:$min%02d:$sec%02d"
    }

    var drops = 0
    while (score > config.timeLimitSeconds) {
      checkpoint = dropHeaviest(checkpoint)
      val eval = metric.eval(pds, startSeq ++ checkpoint ++ finishSeq)
      score = eval
      drops += 1
    }

    println(s"Improved to ${timePrint(score.round)} at step ${totalSteps} and drops ${drops} and score ${metric.evalScore(startSeq ++ checkpoint ++ finishSeq)}")

    def dropHeaviest(points: Seq[RoutePoint]): Seq[RoutePoint] = {
      val maxWeightIdx = getDropWeights(points).zipWithIndex.maxBy(_._1)._2
      points.slice(0, maxWeightIdx) ++ points.slice(maxWeightIdx + 1, points.size)
    }

    def getDropWeights(points: Seq[RoutePoint]): Seq[Double] = {
      points.slice(0, points.size - 2) lazyZip points.slice(1, points.size - 1) lazyZip points.slice(2, points.size) map {
        case (prev, drop, after) => (
          pds.distanceBetween(prev, drop)
            + pds.distanceBetween(drop, after)
            - pds.distanceBetween(prev, after)
          ) / drop.score
      }
    }

    OptimalRoute(
      startSeq ++ checkpoint ++ finishSeq,
      score.round,
      metric.evalScore(startSeq ++ checkpoint ++ finishSeq).round
    )
  }
}
