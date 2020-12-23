package lv.martovs.routePlanner

import lv.martovs.routePlanner.distanceService.{DirectDistanceService, DistanceService, OsrmDistanceService}
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

    Future[Unit] {
      OsrmDistanceService.initForPoints(task.config.points) match {
        case None => Future.failed(new RuntimeException("API error"))
        case Some(distanceService) =>
          for (i <- 0L to task.config.iterationCount) {
            val rm = RouteOptimizer.optimize(task.config, distanceService, Mutator.mutate[RoutePoint], DistanceMetric)
            if (i == 0 || {
              val prevRunPoints = store.get(task.id).get.optimalRoute.get.seq
              DistanceMetric.evalScore(rm.seq) > DistanceMetric.evalScore(prevRunPoints)
            }) {
              println("Update")
              store.update(task.id, task.copy(optimalRoute = Some(rm), status = TaskItemStatus.Improving))
            }
          }
          store.update(task.id, store.get(task.id).get.copy(status = TaskItemStatus.Stopped))
          Future.successful()
      }
    }
  }
}
