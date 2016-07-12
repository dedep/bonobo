import play.PlayScala

lazy val bonobo = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """bonobo""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    fork in Test := true,
    updateOptions := updateOptions.value.withCachedResolution(true),
    javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled"),
    libraryDependencies ++= Seq(
      ws,
      "com.typesafe.play" %% "play-slick" % "0.8.1",
      "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
      "org.scalatest" %% "scalatest" % "2.2.0",
      "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
      "joda-time" % "joda-time" % "2.4",
      "org.joda" % "joda-convert" % "1.6",
      "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
      "org.apache.commons" % "commons-math3" % "3.2",
      "com.typesafe.slick" %% "slick-codegen" % "2.1.0",
      "org.scaldi" %% "scaldi-play" % "0.3.3")
  )
