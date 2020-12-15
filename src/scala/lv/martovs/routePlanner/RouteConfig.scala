package lv.martovs.routePlanner

case class RouteConfig(
                        lockedStartPointIds: Seq[String],
                        lockedFinishPointIds: Seq[String],
                        points: Set[RoutePoint],
                        timeLimitSeconds: Long
                      )
