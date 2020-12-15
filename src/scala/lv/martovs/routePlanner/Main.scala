package lv.martovs.routePlanner

import scala.annotation.tailrec

object Main extends App {
  println("Hello")

  var rm = new RouteMaker(RouteConfig(
    lockedStartPointIds = Seq("a"),
    lockedFinishPointIds = Seq("e"),
    points = Set(
      RoutePoint("a", 56.80, 24.21, 1),
      RoutePoint("b", 56.88, 24.12, 1),
      RoutePoint("c", 56.90, 24.20, 1),
      RoutePoint("e", 56.85, 24.15, 1),
    ),
    1 * 60 * 60,
  ))
}

case class EvaluationResult(
                             totalScore: Double,
                             totalConsumption: Double
                           )

class RouteMaker(config: RouteConfig) {
  val pds: PointDistanceService = new PointDistanceDirect()
  // validate all points in config locked are listed in points section

  def resolvePointById(pId: String): RoutePoint = config.points.find(point => point.id == pId).get

  val startSeq = config.lockedStartPointIds.map(resolvePointById)
  val finishSeq = config.lockedFinishPointIds.map(resolvePointById)
  val middlePoints = config.points
    .filterNot(p => config.lockedStartPointIds.contains(p.id) || config.lockedFinishPointIds.contains(p.id))

  val middleSeq = scala.util.Random.shuffle(middlePoints).toSeq
  println(middleSeq)

  var checkpoint = middleSeq
  var score = Double.MaxValue
  for (i <- 0 to 10) {
    val candidate = mutate(checkpoint)
    val eval = evaluate((startSeq ++ candidate ++ finishSeq).toList)
    if (eval.totalConsumption < score) {
      score = eval.totalConsumption
      checkpoint = candidate
      println(s"Improved to ${score}")
    }
  }

  println("Fin")


  def mutate(points: Seq[RoutePoint]): Seq[RoutePoint] = {

    def pickAB(size: Int): (Int, Int) = {
      /*
      * A B C D E F G H
      * 0 1 2 3 4 5 6 7
      *
      * size = 8
      * a=4
      * off = [1..7)
      * b = [5..7]+[0..3)
      *
      * */

      val a = scala.util.Random.between(0, size)
      val offset = scala.util.Random.between(1, size)
      val b = (a + offset) % size
      (math.min(a, b), math.max(a, b))
    }

    def flip(points: Seq[RoutePoint]): Seq[RoutePoint] = {
      val (a, b) = pickAB(points.size)
      points.slice(0, a) ++ points.slice(a, b).reverse ++ points.slice(a, points.size)
    }

    def nop(points: Seq[RoutePoint]): Seq[RoutePoint] = {
      points
    }

    scala.util.Random.between(0, 1.0) match {
      case n if n < 0.75 => flip(points)
      case n => nop(points)
    }
  }


  def pairZip[A](list: List[A]): List[(A, A)] = {
    val length = list.length
    list.slice(0, length - 1).zip(list.slice(1, length))
  }

  private def evaluate(points: List[RoutePoint]): EvaluationResult = {
    val score = points.map(p => p.score).sum
    val pairs = pairZip(points)
    val consumption = pairs.map({
      case (p1, p2) => pds.distanceBetween(p1, p2)
    }).sum
    EvaluationResult(score, consumption)
  }
}

