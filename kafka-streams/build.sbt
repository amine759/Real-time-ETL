name := "kafka-streams"
version := "0.1"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % "3.5.2",
  "org.apache.kafka" % "kafka-clients" % "3.5.2",
  "org.scala-lang" % "scala-library" % "2.13.14",
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.10.0"
)

resolvers += "Maven Central" at "https://repo1.maven.org/maven2/"

// Add any other dependencies you need
