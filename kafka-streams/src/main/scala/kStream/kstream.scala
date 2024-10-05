package kStream

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.JoinWindows
import java.util.Properties
import scala.util.Try
import java.time.{Instant, Duration}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.clients.consumer.ConsumerConfig

case class CarbonData(ts: Double, device: String, co: Double)
case class TempData(ts: Double, device: String, temp: Double)

object Kstream extends App {
  // Define the configuration for the Streams application
  val props: Properties = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app-v3")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") 
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Add this line
  // Create a StreamsBuilder
  val builder: StreamsBuilder = new StreamsBuilder()

  val topic1 = "topic1"
  val topic2 = "topic2"

  // Create a stream for each topic
  val topic1Stream: KStream[String, String] = builder.stream[String, String](topic1)
  val topic2Stream: KStream[String, String] = builder.stream[String, String](topic2)

  // Merge the streams into one
  val allTopicsStream: KStream[String, String] = topic1Stream.merge(topic2Stream)


  allTopicsStream.foreach { (key, value) =>
    val transformedData = transformValue(value)
    println(s"[Kafka Streams] Consumed and transformed message: Key = $key, Value = $transformedData")
  }
  
  // Build the topology
  val topology: Topology = builder.build()
  val streams: KafkaStreams = new KafkaStreams(topology, props)

  // Start the Streams application
  try {
    streams.start()
    println("Kafka Streams app started")
  } catch {
    case e: Exception =>
      println(s"Error starting Kafka Streams: ${e.getMessage}")
      e.printStackTrace()
  }
  // Add shutdown hook to gracefully close the Streams application
  sys.addShutdownHook {
    println("Shutting down Kafka Streams application...")
    streams.close()
  }


  while (true) {
    Thread.sleep(1000) // Sleep for 1 second

  }
  // Helper function to parse and transform the ts column from Unix time to datetime
  def transformValue(value: String): String = {
    // Parse the JSON string
    val jsonEither = parse(value)

    jsonEither match {
      case Left(error) =>
        s"Error parsing JSON: ${error.getMessage}"

      case Right(json) =>
        // Check if it contains "co" or "temp" and decode accordingly
        if (json.hcursor.get[Double]("co").isRight) {
          json.as[CarbonData] match {
            case Left(error) =>
              s"Error decoding JSON as CarbonData: ${error.getMessage}"

            case Right(carbonData) =>
              // Convert the timestamp to a human-readable format
              val readableDate = parseTimestamp(carbonData.ts)
              s"Carbon Data -> Device: ${carbonData.device}, CO: ${carbonData.co}, Timestamp: $readableDate"
          }
        } else if (json.hcursor.get[Double]("temp").isRight) {
          json.as[TempData] match {
            case Left(error) =>
              s"Error decoding JSON as TempData: ${error.getMessage}"

            case Right(tempData) =>
              // Convert the timestamp to a human-readable format
              val readableDate = parseTimestamp(tempData.ts)
              s"Temperature Data -> Device: ${tempData.device}, Temp: ${tempData.temp}, Timestamp: $readableDate"
          }
        } else {
          "Unknown data format"
        }
    }
  }
  // Helper function to parse and transform the ts column from Unix time to datetime
  def parseTimestamp(ts: Double): String = {
    Try(Instant.ofEpochSecond(ts.toLong).toString).getOrElse("Invalid Timestamp")
  }
}
