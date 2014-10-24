package utils

object CityImporter extends App {
  import scala.io.Source

  Source.fromFile(args(0)).getLines().map(
    line => (line.split("\t").head, line.split("\t").last.replace(",", ""))
  ).foreach(
      line => println("INSERT INTO cities(name, population, container, latitude, longitude) " +
        "VALUES ('" + line._1 + "', " + line._2 + ", " + args(1) + ", 0, 0);")
    )
}
