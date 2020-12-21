package lv.martovs.routePlanner.store

import java.util.concurrent.atomic.AtomicReference

import scala.collection.mutable.ListBuffer

class Storage {
  type It = Task.Item
  type Id = Task.Id

  val store: AtomicReference[ListBuffer[It]] = new AtomicReference[ListBuffer[It]](new ListBuffer[It]())

  def add(item: It): Unit = {
    store.updateAndGet(list => list.append(item))
  }

  def get(id: Id): Option[It] = {
    store.get().find(item => item.id == id)
  }

  def update(id: Id, newValue: It): Unit = {
    store.updateAndGet(list => {
      list.indexWhere(item => item.id == id) match {
        case -1 => ()
        case idx => list.update(idx, newValue)
      }
      list
    })
  }

  def getPending(): Set[Task.Item] = {
    store.get().filter(it => it.status == TaskItemStatus.Pending).toSet
  }
}

object TaskStore extends Storage
