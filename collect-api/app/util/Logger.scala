package util

import org.slf4j.LoggerFactory

trait Logger {
  protected val logger = LoggerFactory.getLogger(this.getClass)
}
