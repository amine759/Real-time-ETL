package consumer

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.{Collections, Properties}
import scala.collection.JavaConverters._

object Consumer extends App {
  // Consumer configuration
  val props: Properties = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "csv-consumer-group")

  // Create Kafka consumer
  val consumer = new KafkaConsumer[String, String](props)

  // Subscribe to multiple topics
  consumer.subscribe(java.util.Arrays.asList("topic1", "topic2"))

  // Poll for new data from both topics
  while (true) {
    val records = consumer.poll(java.time.Duration.ofMillis(1000))
    for (record <- records.asScala) {
      println(s"Consumed message from topic ${record.topic()}: ${record.value()}")
    }
  }
}
