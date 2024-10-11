package producer

case class AppConfig(
    kafkaBootstrapServers: String,
)

object Config {
  def load(): AppConfig = {
    AppConfig(
      kafkaBootstrapServers = "broker:9092",
    )
  }
}
