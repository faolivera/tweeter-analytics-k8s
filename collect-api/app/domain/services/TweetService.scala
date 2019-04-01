package domain.services

import java.time.Clock

import domain.models.RichTweet
import javax.inject.Inject
import rest.models.Tweet

trait TweetService {

  def persist(tweet: Tweet, userId: Option[String], userIp: Option[String])

  class TweetServiceImpl @Inject()(kafkaClientService: KafkaClientService,
                                   idService: IdService,
                                   clock: Clock)
      extends TweetService {

    override def persist(tweet: Tweet,
                         userId: Option[String],
                         userIp: Option[String]): Unit = {

      val richTweet = RichTweet(
        id = idService.getUniqueId(),
        userId = userId.getOrElse("Anonymous"),
        tweet = tweet,
        createdAt = clock.instant(),
        clientIp = userIp.getOrElse("anonymous-ip")
      )
      kafkaClientService.send(richTweet)
    }
  }
}
