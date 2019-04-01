package domain.services

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

trait JsonSerializer {
  import JsonSerializer._
  def serialize[A: Writes](value: A): String = {
    Json.toJson(value).toString()
  }

  def deserialize[A: Reads](value: String): Try[A] = {
    try {
      Json.parse(value).validate[A] match {
        case JsSuccess(deserialized, _) => Success(deserialized)
        case JsError(errors) =>
          Failure(DeserializationException(makeErrorDetail(errors)))
      }
    } catch {
      case e: Exception =>
        Failure(DeserializationException(e.getMessage))
    }

  }

  private def makeErrorDetail(
      errors: Seq[(JsPath, Seq[JsonValidationError])]): String = {
    errors
      .map {
        case (path, validationErrors) =>
          val errors = validationErrors.flatMap(_.messages).mkString(", ")
          s"$path: $errors"
      }
      .mkString("; ")
  }
}

object JsonSerializer {
  case class DeserializationException(msg: String) extends RuntimeException(msg)
}
