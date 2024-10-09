lazy val root = (project in file("."))
  .aggregate(consumer, producer, kStream, example)
  .settings(
    name := "microservices-project",
    version := "0.1.0",
    calaVersion := "2.13.12"
  )

lazy val consumer = (project in file("consumer"))
  .settings(
    name := "consumer-service",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
      "com.typesafe" % "config" % "1.4.1",
      "org.elasticsearch.client" %% "elasticsearch-rest-client" % "7.10.0"
      // Add other dependencies as needed
    )
  )

lazy val producer = (project in file("producer"))
  .settings(
    name := "producer-service",
    libraryDependencies ++= Seq(
      // Producer-specific dependencies
    )
  )

lazy val kStream = (project in file("kStream"))
  .settings(
    name := "kStream-service",
    libraryDependencies ++= Seq(
      // kStream-specific dependencies
    )
  )

lazy val example = (project in file("example"))
  .settings(
    name := "example-service",
    libraryDependencies ++= Seq(
      // Example-specific dependencies
    )
  )
