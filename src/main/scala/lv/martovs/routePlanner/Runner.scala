package lv.martovs.routePlanner

import lv.martovs.routePlanner.Main.{pds, routeConfig}

object Runner {
  val pds: PointDistanceService = new PointDistanceDirect()


  def notify(store: Storage): Unit = {
    store.getPending().foreach(this.run)
  }

  def run(task: Task.Item): Unit = {
    println("Started task", task.id)
    val rm = new RouteMaker(task.config, pds, Mutators.mutate[RoutePoint], DistanceMetric)
    TaskStore.update(task.id, task.copy(pointSequence = Some(rm.getOutput())))
  }
}
