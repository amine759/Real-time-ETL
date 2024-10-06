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
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Start reading at the earliest offset

  // Create Kafka consumer
  val consumer = new KafkaConsumer[String, String](props)

  // Subscribe to the trans-topic
  consumer.subscribe(java.util.Arrays.asList("trans-topic"))

  // Add shutdown hook to close the consumer gracefully
  sys.addShutdownHook {
    println("Shutting down the consumer...")
    consumer.wakeup() // Interrupt the polling loop if it's blocked
    consumer.close() // Close the consumer
  }

  // Poll for new data from the topic
  try {
    while (true) {
      val records = consumer.poll(java.time.Duration.ofMillis(1000))
      for (record <- records.asScala) {
        println(s"Consumed message from topic ${record.topic()}: ${record.value()}")
      }
    }
  } catch {
    case e: Exception =>
      println(s"Error while consuming messages: ${e.getMessage}")
      e.printStackTrace()
  } finally {
    consumer.close() // Ensure the consumer is closed
  }
}
