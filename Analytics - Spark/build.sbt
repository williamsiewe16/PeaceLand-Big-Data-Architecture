name := "Spark Analytics"

version := "1.0"

scalaVersion := "2.13.8"
val sparkVersion = "3.2.1"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion

cancelable in Global := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor-typed_2.13" % "2.6.14",
  "com.typesafe.akka" % "akka-stream-typed_2.13" % "2.6.14",
  "com.typesafe.akka" % "akka-http_2.13" % "10.2.4",
  "com.typesafe.akka" % "akka-http-spray-json_2.13" % "10.2.4",
  "ch.megard" % "akka-http-cors_2.13" % "1.1.1"
)