import models.{MatchTraitRepo, SchemaDefinition}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, JsString, Json}
import sangria.ast.Document

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import sangria.macros._
import sangria.execution.Executor
import sangria.execution.deferred.DeferredResolver
import sangria.marshalling.playJson._

class SchemaSpec extends AnyWordSpec with Matchers {
  "StartWars Schema" should {
    "correctly identify R2-D2 as the hero of the Star Wars Saga" in {
      val query =
        graphql"""
         query HeroNameQuery {
           hero {
             name
           }
         }
       """

      executeQuery(query) should be (Json.parse(
        """
         {
           "data": {
             "hero": {
               "name": "R2-D2"
             }
           }
         }
        """))
    }

    "allow to fetch Han Solo using his ID provided through variables" in {
      val query =
        graphql"""
         query FetchSomeIDQuery($$matchId: String!) {
           match(id: $$matchId) {
             name
           }
         }
       """

      executeQuery(query, vars = Json.obj("matchId" → JsString("1002"))) should be (Json.parse(
        """
         {
           "data": {
             "match": {
               "name": "Han Solo"
             }
           }
         }
        """))
    }
  }

  def executeQuery(query: Document, vars: JsObject = Json.obj()) = {
    val futureResult = Executor.execute(SchemaDefinition.StarWarsSchema, query,
      variables = vars,
      userContext = new MatchTraitRepo,
      deferredResolver = DeferredResolver.fetchers(SchemaDefinition.characters))

    Await.result(futureResult, 10.seconds)
  }
}
