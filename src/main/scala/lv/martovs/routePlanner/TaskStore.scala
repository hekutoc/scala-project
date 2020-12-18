package lv.martovs.routePlanner

import scala.collection.mutable.ListBuffer

sealed trait TaskItemStatus

object TaskItemStatus {

  final case object Pending extends TaskItemStatus

  final case object Running extends TaskItemStatus

  final case object Improving extends TaskItemStatus

  final case object Stopped extends TaskItemStatus

}


object TaskStore {
  type TaskId = String

  case class TaskItem(id: TaskId, status: TaskItemStatus, config: RouteConfig)

  val store: ListBuffer[TaskItem] = new ListBuffer[TaskItem]()

  def add(config: RouteConfig): TaskId = {

    val taskId = store.size.toString
    store.append(TaskItem(taskId, TaskItemStatus.Pending, config))
    taskId
  }

  def get(taskId: TaskId): Option[TaskItem] = {
    store.find(s => s.id == taskId).get
  }

}
