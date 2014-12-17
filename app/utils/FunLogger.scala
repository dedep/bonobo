package utils

import com.typesafe.scalalogging.slf4j.Logger

object FunLogger {
  implicit class FLogger[+A](x: A) {
    case class FunctionalLoggingFacade(logger: Logger, message: String, cause: Option[Throwable], args: AnyRef*) {
      def error(): A = {
        if (cause.nonEmpty) logger.error(message, cause.get)
        else if (args.nonEmpty) logger.error(message, args)
        else logger.error(message)

        x
      }

      def warn(): A = {
        if (cause.nonEmpty) logger.warn(message, cause.get)
        else if (args.nonEmpty) logger.warn(message, args)
        else logger.warn(message)

        x
      }

      def info(): A = {
        if (cause.nonEmpty) logger.info(message, cause.get)
        else if (args.nonEmpty) logger.info(message, args)
        else logger.info(message)

        x
      }

      def debug(): A = {
        if (cause.nonEmpty) logger.debug(message, cause.get)
        else if (args.nonEmpty) logger.debug(message, args)
        else logger.debug(message)

        x
      }

      def trace(): A = {
        if (cause.nonEmpty) logger.trace(message, cause.get)
        else if (args.nonEmpty) logger.trace(message, args)
        else logger.trace(message)

        x
      }
    }

    def log(message: A => Any)(implicit logger: Logger): FunctionalLoggingFacade =
      FunctionalLoggingFacade(logger, message(x).toString, None, Seq())

    def log(message: A => Any, cause: Throwable)(implicit logger: Logger): FunctionalLoggingFacade =
      FunctionalLoggingFacade(logger, message(x).toString, Some(cause), Seq())

    def log(message: A => Any, args: Seq[AnyRef])(implicit logger: Logger): FunctionalLoggingFacade =
      FunctionalLoggingFacade(logger, message(x).toString, None, args)
  }
}
