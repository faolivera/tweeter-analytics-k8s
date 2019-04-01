package domain

import play.api.libs.json.Json

package object models {
  implicit val richTweetFormat = Json.format[RichTweet]
}
