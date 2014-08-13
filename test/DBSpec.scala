package test

import models.table.CitiesTable
import org.specs2.mutable._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._


class DBSpec extends Specification {

//  "List" should {
//    "return all records" in new play.api.test.WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
//      val cats = TableQuery[CitiesTable]
//      //see a way to architect your app in the computers-database play-slick sample
//      //http://github.com/playframework/play-slick/tree/master/samples/play-slick-sample
//
//      DB.withSession { implicit s: Session =>
//        val testKitties = Seq(
//          (0l, "Lublin", 500, 500, 1l),
//          (1l, "Lublin", 500, 500, 1l),
//          (2l, "Gublin", 500, 500, 1l))
//
//        cats.insertAll(testKitties: _*)
//        cats.list must equalTo(testKitties)
//      }
//    }
//  }
//}


//class DBSpec extends Specification {

//  "DB" should {
//    "work as expected" in new WithApplication {
//
//      //create an instance of the table
//      val cats = TableQuery[CitiesTable]
//      //see a way to architect your app in the computers-database play-slick sample
//      //http://github.com/playframework/play-slick/tree/master/samples/play-slick-sample
//
//      DB.withSession { implicit s: Session =>
//        val testKitties = Seq(
//          (0l, "Lublin", 500, 500, 1l),
//          (1l, "Lublin", 500, 500, 1l),
//          (2l, "Gublin", 500, 500, 1l))
//
//        cats.insertAll(testKitties: _*)
//        cats.list must equalTo(testKitties)
//      }
//    }

//    "select the correct testing db settings by default" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
//      DB.withSession { implicit s: Session =>
//        s.conn.getMetaData.getURL must startWith("jdbc:h2:mem:play-test")
//      }
//    }

//  "use the correct db settings when specified" in new WithApplication {
//    play.api.db.slick.DB("specific").withSession{ implicit s:Session =>
//      s.conn.getMetaData.getURL must equalTo("jdbc:h2:mem:veryspecialindeed")
//    }
//  }

//    "use the default db settings when no other possible options are available" in new WithApplication {
//      DB.withSession { implicit s: Session =>
//        s.conn.getMetaData.getURL must equalTo("jdbc:h2:mem:play")
//      }
//    }
//  }

//}
