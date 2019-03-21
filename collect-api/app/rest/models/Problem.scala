package rest.models

import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{Format, Json, JsonConfiguration, JsonNaming}

case class Problem(status: Int,
                   title: String,
                   problemType: String,
                   detail: Option[String],
                   instance: Option[String] = None)

object Problem {
  def apply(status: Int, title: String): Problem =
    Problem(status, title, s"http://httpstatus.es/$status", None)

  def apply(status: Int, title: String, detail: String): Problem =
    Problem(status, title, s"http://httpstatus.es/$status", Some(detail))

}
