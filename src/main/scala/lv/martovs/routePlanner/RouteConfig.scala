package lv.martovs.routePlanner

case class RouteConfig(
                        lockedStartPointIds: Seq[String],
                        lockedFinishPointIds: Seq[String],
                        points: Set[RoutePoint],
                        timeLimitSeconds: Long
                      )

object RouteConfig {
  def default = RouteConfig(
    lockedStartPointIds = Seq("Start"),
    lockedFinishPointIds = Seq("Finish"),
    points = Set(
      RoutePoint("Start", 56.379082, 24.177475, 0),
      RoutePoint("Finish", 56.360591, 24.28412, 0),
      RoutePoint("1", 56.598744, 24.071474, 105),
      RoutePoint("2", 56.596942, 24.350467, 400),
      RoutePoint("3", 56.594745, 24.205531, 105),
      RoutePoint("4", 56.513018, 24.561825, 100),
      RoutePoint("5", 56.427573, 24.475822, 100),
      RoutePoint("6", 56.465149, 24.170185, 105),
      RoutePoint("7", 56.383502, 24.251118, 105),
      RoutePoint("8", 56.316299, 24.251375, 200),
      RoutePoint("9", 56.587473, 24.444108, 100),
      RoutePoint("10", 56.331481, 24.194384, 100),
      RoutePoint("11", 56.451393, 24.598389, 200),
      RoutePoint("12", 56.440576, 24.052538, 110),
      RoutePoint("13", 56.597013, 24.189046, 110),
      RoutePoint("14", 56.512675, 24.031992, 100),
      RoutePoint("15", 56.432319, 24.334888, 105),
      RoutePoint("16", 56.418246, 24.1961, 200),
      RoutePoint("17", 56.526297, 24.473945, 125),
      RoutePoint("18", 56.44063, 24.045746, 125),
      RoutePoint("19", 56.597988, 24.532857, 125),
      RoutePoint("20", 56.507844, 24.100485, 105),
    ),
    5 * 60 * 60,
  )

}
