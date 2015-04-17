package controllers.validator

class ValidationException(message: String = null, cause: Throwable = null) extends Exception(message, cause)