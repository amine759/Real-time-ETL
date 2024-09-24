package kStream

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.kstream.{KStream, Consumed}
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.JoinWindows
import java.util.Properties
import scala.concurrent.duration._
import org.apache.kafka.streams.Topology
import scala.util.Try
import java.time.{Instant, Duration}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._


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
        // Parse and transform the ts column from Unix time to datetime
        val ts1 = parseTimestamp(value1) // Implement this function to extract and convert ts
        val ts2 = parseTimestamp(value2) // Implement this function to extract and convert ts
        val transformedData = s"Topic1: $value1, Topic2: $value2, JoinedTS: $ts1, $ts2"
        
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
  def parseTimestamp(record: String): String = {
    // Assuming the record is a CSV line, extract the ts column (e.g., from the first position)
    val tsValue = record.split(",")(0) // Change the index based on your CSV format
    Try(Instant.ofEpochSecond(tsValue.toLong).toString).getOrElse("Invalid Timestamp")
  }
}
