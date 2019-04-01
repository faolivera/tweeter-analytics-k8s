package domain.services

import domain.models.RichTweet
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

trait KafkaClientService {
  def send(tweet: RichTweet): Boolean
}

object KafkaClientService {
  val RICH_TWEET_TOPIC = "collect-api.rich-tweet"
}

class KafkaClientServiceImpl(kafkaProducer: KafkaProducer[Long, String])
    extends JsonSerializer {
  import KafkaClientService._

  def send(tweet: RichTweet): Boolean = {

    val record = new ProducerRecord[Long, String](RICH_TWEET_TOPIC,
                                                  tweet.id,
                                                  serialize(tweet))
    kafkaProducer.send(record)
  }
}
