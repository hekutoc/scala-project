package lv.martovs.routePlanner.store

import io.circe.Encoder
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import lv.martovs.routePlanner.RouteConfig
import io.circe.generic.auto._
import lv.martovs.routePlanner.RouteOptimizer.OptimalRoute

sealed trait TaskItemStatus

object TaskItemStatus {
  final case object Pending extends TaskItemStatus
  final case object Running extends TaskItemStatus
  final case object Improving extends TaskItemStatus
  final case object Stopped extends TaskItemStatus
}

object Task {
  type Id = String

  @JsonCodec case class Item(
                   id: Id,
                   status: TaskItemStatus,
                   config: RouteConfig,
                   optimalRoute: Option[OptimalRoute] = None
                 )

  implicit val encoderTaskItemStatus: Encoder[TaskItemStatus] = {
    case TaskItemStatus.Pending => "pending".asJson
    case TaskItemStatus.Stopped => "stopped".asJson
    case TaskItemStatus.Running => "running".asJson
    case TaskItemStatus.Improving => "improving".asJson
  }

  implicit val encoder: Encoder[Task.Item] = deriveEncoder[Task.Item]
}