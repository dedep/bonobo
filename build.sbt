name := """bonobo"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.2",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.10" % "2.1.2",
  "org.scalatest" % "scalatest_2.10" % "2.2.0",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41")

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)