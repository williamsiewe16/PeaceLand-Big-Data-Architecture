name := "PeaceWatcher"

version := "1.0"

scalaVersion := "2.13.8"
val sparkVersion = "3.2.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.8.0",
  "org.json4s" %% "json4s-native" % "3.5.5"
)
