package models.dedep.bonobo.core.utils

object MathUtils {
  def isPowerOfTwo(num: Int): Boolean =
    if (num < 1) false
    else getFloorPowerOfTwoNumber(num) == num

  def getFloorPowerOfTwoNumber(number: Int): Int = {
    require(number >= 1)
    Stream.iterate(1) {
      _ * 2
    }.takeWhile(_ <= number).last
  }
}
