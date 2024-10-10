// /kafka-streams/build.sbt


import sbt._
import Keys._
import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport._
import com.typesafe.sbt.packager.JavaAppPackaging

lazy val root = (project in file("."))
  .aggregate(consumer, producer, kStream)
  .settings(
    name := "kafka-streams-microservices",
    version := "0.1.0",
    scalaVersion := "2.12.19",
    libraryDependencies ++= Seq(
      commonConfig // Common dependency across microservices
      // Add other common dependencies here if needed
    ),
    resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"
  )
  .enablePlugins(JavaAppPackaging) // Enable the native packager plugin

lazy val consumer = (project in file("consumer"))
  .settings(
    name := "consumer-service",
    libraryDependencies ++= Seq(
      kafkaStreamsScala,
      kafkaClients,
      logbackClassic,
      scalaCollectionCompat,
      circeCore,
      circeGeneric,
      circeParser,
      elasticsearchRestClient,
      munit
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    assembly / mainClass := Some("consumer.Consumer") // Replace with your actual main class
  )

lazy val producer = (project in file("producer"))
  .settings(
    name := "producer-service",
    libraryDependencies ++= Seq(
      kafkaStreamsScala,
      kafkaClients,
      logbackClassic,
      scalaCollectionCompat,
      circeCore,
      circeGeneric,
      circeParser,
      munit
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    assembly / mainClass := Some("producer.Producer") // Replace with your actual main class
  )
  .enablePlugins(JavaAppPackaging) // Enable the native packager plugin for producer

lazy val kStream = (project in file("kStream"))
  .settings(
    name := "kStream-service",
    libraryDependencies ++= Seq(
      kafkaStreamsScala,
      kafkaClients,
      logbackClassic,
      scalaCollectionCompat,
      circeCore,
      circeGeneric,
      circeParser,
      munit
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    },
    assembly / mainClass := Some("kStream.Kstream") // Replace with your actual main class
  )
  .enablePlugins(JavaAppPackaging) // Enable the native packager plugin for kStream
 )
