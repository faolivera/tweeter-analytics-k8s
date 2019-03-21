package rest.models

case class Tweet(text: String,
                 inReplyToTweetId: Long,
                 retweetTweetId: Long,
                 geoLocation: Option[String])
