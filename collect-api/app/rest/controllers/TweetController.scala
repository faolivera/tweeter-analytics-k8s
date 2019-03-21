package rest.controllers

import domain.services.TweetService
import javax.inject.Inject
import play.api.mvc.InjectedController
import rest.models.Tweet
import rest.ActionHelpers.fromJson

class TweetController @Inject()(tweetService: TweetService)
    extends InjectedController {

  def create() = Action { implicit request =>
    fromJson { tweet: Tweet =>
      val userId = request.headers.get("User")
      val clientIp = request.headers
        .get(X_FORWARDED_FOR)
        .flatMap(_.split(",").headOption.map(_.trim))

      tweetService.persist(tweet, userId, clientIp)
      Created
    }
  }

}
