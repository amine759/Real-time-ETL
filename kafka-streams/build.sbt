name := "kafka-streams"

version := "1.0"

scalaVersion := "3.3.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "3.5.1",     // Kafka client library
  "org.apache.kafka" % "kafka-streams" % "3.5.1",     // Kafka Streams library
  "org.slf4j" % "slf4j-simple" % "1.7.36",             // SLF4J for logging
  "com.typesafe.play" %% "play-json" % "2.10.0-RC7"    // Play JSON library for JSON parsing
)
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
