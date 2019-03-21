package rest

import play.api.libs.json.{Format, Json, JsonConfiguration, JsonNaming}
import play.api.libs.json.JsonNaming.SnakeCase

package object models {
  implicit val config = JsonConfiguration[Json.WithDefaultValues](SnakeCase)

  implicit val problemFormat: Format[Problem] = Json.format[Problem]
  implicit val tweetFormat: Format[Tweet] = Json.format[Tweet]
}
