package producer
import java.io.{FileNotFoundException, IOException}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import scala.io.Source
import java.util.Properties
import io.circe.generic.auto._
import io.circe.syntax._
import java.util.concurrent.{Executors, Future, TimeUnit}

// Case class for Topic 1
case class CarbonData(ts: Double, device: String, co: Double)
case class TempData(ts: Double, device: String, temp: Double)

object Producer extends App {
  // Producer configuration
  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

  // Create Kafka producer
  val producer = new KafkaProducer[String, String](props)

  // Function to read CSV and send data to Kafka topic
  def sendCsvDataToTopic(filePath: String, topic: String, producer: KafkaProducer[String, String]): Unit = {
    try {
      // Attempt to read the CSV file
      val source = Source.fromFile(filePath)

      // Send each line (skipping the header) to the specified Kafka topic
      for (line <- source.getLines().drop(1)) {
        // Split the line by commas and adjust for the ID column
        val columns = line.split(",")
        if (columns.length >= 4) { // Ensure there are enough columns
          val ts = columns(1).toDouble // Use the second column for ts
          val device = columns(2) // Use the third column for device
          val json = if (topic == "topic1") {
            val co = columns(3).toDouble // Use the fourth column for co
            val carbonData = CarbonData(ts, device, co)
            carbonData.asJson.noSpaces
          } else {
            val temp = columns(3).toDouble // Use the fourth column for temp
            val tempData = TempData(ts, device, temp)
            tempData.asJson.noSpaces
          }

          // Send the record to Kafka
          val record = new ProducerRecord[String, String](topic, null, json)
        producer.send(record)
          println(s"Sent to $topic: $json") // Display the data being sent
        } else {
          println(s"Error: Unexpected line format in file '$filePath': $line")
        }
      }
      source.close() // Don't forget to close the source!

    } catch {
      case e: FileNotFoundException =>
        println(s"Error: File '$filePath' not found.")
      case e: IOException =>
        println(s"Error: Unable to read file '$filePath'. ${e.getMessage}")
      case e: Exception =>
        println(s"Error: An unexpected error occurred while processing file '$filePath'. ${e.getMessage}")
    }
  }

  println("Current Working Directory: " + new java.io.File(".").getCanonicalPath)

  val executor = Executors.newFixedThreadPool(2) // 2 threads for 2 topics
  val futures: Seq[Future[_]] = Seq(
    executor.submit(new Runnable {
      override def run(): Unit = {
        sendCsvDataToTopic("../archive/carbon.csv", "topic1", producer)
      }
    }),
    executor.submit(new Runnable {
      override def run(): Unit = {
        sendCsvDataToTopic("../archive/temperature.csv", "topic2", producer)
      }
    })
  )

  // Shutdown executor and wait for tasks to finish
  executor.shutdown()
  try {
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
      executor.shutdownNow()
    }
  } catch {
    case e: InterruptedException =>
      executor.shutdownNow()
  }

  // Close the producer after all tasks are completed
  producer.close()
}
