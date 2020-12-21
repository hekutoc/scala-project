package lv.martovs.routePlanner

import lv.martovs.routePlanner.distanceService.{DirectDistanceService, DistanceService, OSRMDistanceService}
import lv.martovs.routePlanner.metrics.DistanceMetric
import lv.martovs.routePlanner.store.{Storage, Task, TaskItemStatus, TaskStore}

import scala.concurrent.Future

object Runner {
  def notify(store: Storage): Unit = {
    store.getPending().foreach(this.run(store))
  }

  def run(store: Storage)(task: Task.Item): Future[Unit] = {
    import concurrent.ExecutionContext.Implicits.global

    println("Started task", task.id)
    store.update(task.id, store.get(task.id).get.copy(status = TaskItemStatus.Running))

    Future {
      val ds: DistanceService = new OSRMDistanceService(task.config.points)

      for (i <- 0 to 10000) {
        val rm = RouteOptimizer.optimize(task.config, ds, Mutator.mutate[RoutePoint], DistanceMetric)


        if (i == 0 || {
          val prevRun = store.get(task.id).get.pointSequence.get
          val prevRunPoints = prevRun.map(p => task.config.points.find(_.id == p).get)
          DistanceMetric.evalScore(rm) < DistanceMetric.evalScore(prevRunPoints)
        }) {
          println("Update")
          store.update(task.id, task.copy(pointSequence = Some(rm.map(_.id))))
        }
      }
      store.update(task.id, store.get(task.id).get.copy(status = TaskItemStatus.Stopped))
    }
  }
}
