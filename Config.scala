package producer

import java.util.Properties
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer

case class ProducerConfig(
    kafkaBootstrapServers: String
)

object Config {
  def load(): ProducerConfig = {
    ProducerConfig(
      kafkaBootstrapServers = "broker:9092" // Use the service name defined in Kubernetes
    )
  }
}
