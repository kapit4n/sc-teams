package models

import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {
  /**
    * Resolves the lists of characters. These resolutions are batched and
    * cached for the duration of a query.
    */
  val characters = Fetcher.caching(
    (ctx: MatchTraitRepo, ids: Seq[String]) =>
      Future.successful(ids.flatMap(id => ctx.getMatch(id))))(HasId(_.id))

  val MatchTrait: InterfaceType[MatchTraitRepo, MatchTrait] =
    InterfaceType(
      "MatchTrait",
      "A character in the Star Wars Trilogy",
      () => fields[MatchTraitRepo, MatchTrait](
        Field("id", StringType,
          Some("The id of the character."),
          resolve = _.value.id),
        Field("name", OptionType(StringType),
          Some("The name of the character."),
          resolve = _.value.name)
      ))

  val Match =
    ObjectType(
      "Match",
      "A matchoid creature in the Star Wars universe.",
      interfaces[MatchTraitRepo, Match](MatchTrait),
      fields[MatchTraitRepo, Match](
        Field("id", StringType,
          Some("The id of the match."),
          resolve = _.value.id),
        Field("name", OptionType(StringType),
          Some("The name of the match."),
          resolve = _.value.name),
      ))

  val ID = Argument("id", StringType, description = "id of the character")

  val Query = ObjectType(
    "Query", fields[MatchTraitRepo, Unit](
      Field("match", OptionType(Match),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.getMatch(ctx arg ID))
    ))

  val StarWarsSchema = Schema(Query)
}
