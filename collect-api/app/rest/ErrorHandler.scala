package rest

import javax.inject.{Inject, Provider, Singleton}
import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND}
import play.api.libs.json.Json
import play.api.mvc.Results.{BadRequest, InternalServerError, NotFound, Status}
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}
import rest.models.Problem
import util.Logger

import scala.concurrent.Future

@Singleton
class ErrorHandler @Inject()(env: Environment,
                             config: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router])
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with Logger {

  override protected def onBadRequest(request: RequestHeader,
                                      message: String): Future[Result] = {
    Future.successful(
      BadRequest(
        Json.toJson(Problem(BAD_REQUEST, InvalidRequestTitle, message))))
  }

  override protected def onNotFound(request: RequestHeader,
                                    message: String): Future[Result] = {
    logger.warn(
      s"Method ${request.method}, path = ${request.path} was not found")
    Future.successful(
      NotFound(
        Json.toJson(
          Problem(NOT_FOUND,
                  ActionNotFoundTitle,
                  s"Method ${request.method}, path = ${request.path}"))
      )
    )
  }

  override protected def onOtherClientError(request: RequestHeader,
                                            statusCode: Int,
                                            message: String): Future[Result] =
    Future.successful(
      Status(statusCode)(
        Json.toJson(Problem(statusCode, InvalidRequestTitle, message))))

  override def onServerError(request: RequestHeader,
                             exception: Throwable): Future[Result] = {
    logger.error(
      s"Internal server error occurred at ${request.method} ${request.path} : ${exception.getMessage}",
      exception)
    Future.successful(
      InternalServerError(
        Json.toJson(Problem(INTERNAL_SERVER_ERROR, InternalServerErrorTitle))
      )
    )
  }

}
