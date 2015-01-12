package com.github.pauldraper.playclosure.play.logging

import play.api.Logger

trait Logging {

  protected val logger = Logger(this.getClass)

}
