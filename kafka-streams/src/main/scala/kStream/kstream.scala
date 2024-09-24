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

case class CarbonData(ts: Double, device: String, co: Double)
case class TempData(ts: Double, device: String, temp: Double)

object Kstream extends App {
  // Define the configuration for the Streams application
  val props: Properties = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass.getName)

  // Create a StreamsBuilder
  val builder: StreamsBuilder = new StreamsBuilder()

  // Consume data from the input topics
  val topic1Stream: KStream[String, String] = builder.stream[String, String]("topic1")
  val topic2Stream: KStream[String, String] = builder.stream[String, String]("topic2")
  
  val joinWindow = JoinWindows.of(Duration.ofMinutes(5)) // Set the join window (e.g., 5 minutes)

  // Join the two streams by the ts column (assuming it's part of the value)
  val joinedStream = topic1Stream
    .join(topic2Stream)(
      (value1, value2) => {
        // Parse the JSON from the value strings
        val carbonData = decode[CarbonData](value1).getOrElse(CarbonData(0.0, "Invalid", 0.0))
        val tempData = decode[TempData](value2).getOrElse(TempData(0.0, "Invalid", 0.0))

        // Transform the timestamp from Unix time to datetime
        val ts1 = parseTimestamp(carbonData.ts) // Extract and convert ts
        val ts2 = parseTimestamp(tempData.ts) // Extract and convert ts
        
        val transformedData = s"Topic1: $carbonData, Topic2: $tempData, JoinedTS: $ts1, $ts2"

        // Display the transformed data
        println(transformedData)

        transformedData // Return the transformed data to produce it later
      },
      joinWindow 
    )

  // Produce the joined stream to the new topic
  joinedStream.to("transform-topic")

  // Build the topology
  val topology: Topology = builder.build()
  val streams: KafkaStreams = new KafkaStreams(topology, props)

  // Start the Streams application
  streams.start()

  // Add shutdown hook to gracefully close the Streams application
  sys.addShutdownHook {
    streams.close()
  }

  // Helper function to parse and transform the ts column from Unix time to datetime
  def parseTimestamp(ts: Double): String = {
    Try(Instant.ofEpochSecond(ts.toLong).toString).getOrElse("Invalid Timestamp")
  }
}
