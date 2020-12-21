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

    def nop(points: Seq[A]): Seq[A] = {
      points
    }

    scala.util.Random.between(0, 1.0) match {
      case n if n < 0.75 => flip(points)
      case n => nop(points)
    }
  }
}
