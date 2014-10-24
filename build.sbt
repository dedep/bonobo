name := """bonobo"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  ws,
  "org.webjars" %% "webjars-play" % "2.2.2",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.10" % "2.1.2",
  "org.scalatest" % "scalatest_2.10" % "2.2.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "joda-time" % "joda-time" % "2.4",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.apache.commons" % "commons-math3" % "3.2",
  "com.typesafe.slick" %% "slick-codegen" % "2.1.0",
  "org.scaldi" % "scaldi-play_2.10" % "0.3.3")

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)