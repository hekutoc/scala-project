package lv.martovs.routePlanner

object Mutator {
  def mutate[A](points: Seq[A]): Seq[A] = {
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

    def flip(points: Seq[A]): Seq[A] = {
      val (a, b) = pickAB(points.size)
      points.slice(0, a) ++ points.slice(a, b).reverse ++ points.slice(b, points.size)
    }

    def drop(points: Seq[A]): Seq[A] = {
      val a = scala.util.Random.between(0, points.size)
      points.slice(0, a) ++ points.slice(a+1, points.size)
    }

    scala.util.Random.between(0, 1.0) match {
      case n if n < 0.90 => flip(points)
      case n => flip(flip(points))
    }
  }
}
