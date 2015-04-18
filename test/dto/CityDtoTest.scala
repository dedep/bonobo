package dto

import dto.model.CityDto
import models.territory.{Territory, City}
import org.specs2.mutable.Specification
import play.api.libs.json.{JsObject, JsString, JsNumber}

class CityDtoTest extends Specification {

  "Should convert city to json" in {
    //given
    val tr = new Territory("TTR", "test-tr", 500100900, None, false, true)
    val t = new City(9, "test-city", 500100, 287, tr, 50.12, 12.5)

    //when
    val result = CityDto.parse(t).toJson

    //then
    result \ "id" mustEqual JsNumber(9)
    result \ "name" mustEqual JsString("test-city")
    result \ "population" mustEqual JsNumber(500100)
    result \ "latitude" mustEqual JsNumber(50.12)
    result \ "longitude" mustEqual JsNumber(12.5)
    result \ "points" mustEqual JsNumber(287)
    result \ "territory" must beAnInstanceOf[JsObject]
    result \ "territory" \ "name" mustEqual JsString("test-tr")
    result \ "territory" \ "code" mustEqual JsString("TTR")
  }
}
