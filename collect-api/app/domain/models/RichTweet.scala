package domain.models

import java.time.Instant
import rest.models.Tweet

case class RichTweet(id: Long,
                     userId: String,
                     twit: Tweet,
                     createdAt: Instant,
                     clientIp: String)
