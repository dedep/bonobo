package models.utils

import org.scalatest.FunSuite
import utils.MathUtils

class MathUtilsTest extends FunSuite {

  test("-1 is not power of 2") {
    assert(!MathUtils.isPowerOfTwo(-1))
  }

  test("0 is not power of 2") {
    assert(!MathUtils.isPowerOfTwo(0))
  }

  test("1 is power of 2") {
    assert(MathUtils.isPowerOfTwo(1))
  }

  test("2 is power of 2") {
    assert(MathUtils.isPowerOfTwo(2))
  }

  test("3 is not power of 2") {
    assert(!MathUtils.isPowerOfTwo(3))
  }

  test("4 is power of 2") {
    assert(MathUtils.isPowerOfTwo(4))
  }

  test("79 is not a power of 2") {
    assert(!MathUtils.isPowerOfTwo(79))
  }

  test("4096 is power of 2") {
    assert(MathUtils.isPowerOfTwo(4096))
  }

  //  test("-1 is not power of 2") {
  //    assert(!MathUtils.isPowerOfTwo(-1))
  //  }

  //  test("0 is not power of 2") {
  //    assert(!MathUtils.isPowerOfTwo(0))
  //  }

  test("1 is highest power of two lower or equal to 1") {
    assert(MathUtils.getFloorPowerOfTwoNumber(1) == 1)
  }

  test("2 is highest power of two lower or equal to 2") {
    assert(MathUtils.getFloorPowerOfTwoNumber(2) == 2)
  }

  test("2 is highest power of two lower or equal to 3") {
    assert(MathUtils.getFloorPowerOfTwoNumber(3) == 2)
  }

  test("4 is highest power of two lower or equal to 4") {
    assert(MathUtils.getFloorPowerOfTwoNumber(4) == 4)
  }

  test("64 is highest power of two lower or equal to 79") {
    assert(MathUtils.getFloorPowerOfTwoNumber(79) == 64)
  }

  test("4096 is highest power of two lower or equal to 4096") {
    assert(MathUtils.getFloorPowerOfTwoNumber(4096) == 4096)
  }
}