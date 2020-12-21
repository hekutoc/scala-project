package lv.martovs.routePlanner.store

import scala.collection.mutable.ListBuffer

class Storage {
  // TODO make thread safe?
  type It = Task.Item
  type Id = Task.Id

  val store: ListBuffer[It] = new ListBuffer[It]()

  def add(item: It): Unit = {
    store.append(item)
  }

  def get(id: Id): Option[It] = {
    store.find(item => item.id == id)
  }

  def update(id: Id, newValue: It): Unit = {
    store.indexWhere(item => item.id == id) match {
      case -1 => ()
      case idx => store.update(idx, newValue)
    }
  }

  def getPending(): Set[Task.Item] = {
    store.filter(it => it.status == TaskItemStatus.Pending).toSet
  }
}

object TaskStore extends Storage
