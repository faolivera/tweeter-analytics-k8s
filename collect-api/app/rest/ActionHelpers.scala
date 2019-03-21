package rest

import play.api.http.Status.{BAD_REQUEST, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsonValidationError, _}
import play.api.mvc.Results.{BadRequest, UnprocessableEntity}
import play.api.mvc.{AnyContent, Request, Result}
import rest.models.Problem

import scala.concurrent.Future

object ActionHelpers {
  def fromJson[T: Reads](block: T => Result)(
      implicit request: Request[AnyContent]): Result = {
    request.body.asJson match {
      case Some(jsValue) =>
        jsValue.validate[T] match {
          case JsSuccess(value, _) => block(value)
          case JsError(errors) =>
            UnprocessableEntity(
              Json.toJson(
                Problem(UNPROCESSABLE_ENTITY,
                        InvalidJsonTitle,
                        makeErrorDetail(errors))))
        }
      case None =>
        BadRequest(Json.toJson(Problem(BAD_REQUEST, InvalidRequestTitle)))
    }
  }

  def fromJson[T: Reads](block: T => Future[Result])(
      implicit request: Request[AnyContent]): Future[Result] = {
    request.body.asJson match {
      case Some(jsValue) =>
        jsValue.validate[T] match {
          case JsSuccess(value, _) => block(value)
          case JsError(errors) =>
            Future.successful(
              UnprocessableEntity(
                Json.toJson(
                  Problem(UNPROCESSABLE_ENTITY,
                          InvalidJsonTitle,
                          makeErrorDetail(errors)))
              )
            )
        }
      case None =>
        Future.successful(
          BadRequest(Json.toJson(Problem(BAD_REQUEST, InvalidRequestTitle))))
    }
  }

  def makeErrorDetail(
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
