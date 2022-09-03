package models

trait MatchTrait {
  def id: String
  def name: Option[String]
}

case class Match(
  id: String,
  name: Option[String],
) extends MatchTrait

class MatchTraitRepo {
  import models.MatchTraitRepo._

  def getMatch(id: String): Option[Match] = matchs.find(c => c.id == id)

}

object MatchTraitRepo {
  val matchs = List(
    Match(
      id = "1000",
      name = Some("Real Madrid vs Barcelona"),
    ),
    Match(
      id = "1001",
      name = Some("Manchester City vs Manchester United"),
    )
  )
}