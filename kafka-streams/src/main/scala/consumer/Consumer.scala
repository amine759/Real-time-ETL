package consumer  
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.Properties
import scala.collection.JavaConverters._

object Consumer extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "csv-consumer-group")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(java.util.Collections.singletonList("csv_topic"))

  while (true) {
    val records = consumer.poll(java.time.Duration.ofSeconds(1))
    for (record <- records.asScala) {
      println(s"Received record: ${record.value()}")
    }
  }
}
