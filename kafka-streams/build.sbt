name := "kafka-streams"
version := "0.1"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % "3.5.2",
  "org.apache.kafka" % "kafka-clients" % "3.5.2",
  "org.scala-lang" % "scala-library" % "2.13.14",
  "ch.qos.logback" % "logback-classic" % "1.2.10", // check for the latest version
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.10.0",
  "io.circe" %% "circe-generic" % "0.14.3",
  "org.scalatestplus" %% "munit" % "0.7.29" % Test,
  "io.circe" %% "circe-parser" % "0.14.3"
)

resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"

// Add any other dependencies you need
