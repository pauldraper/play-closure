package com.github.pauldraper.playclosure.play.controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Iteratee
import play.api.mvc.BodyParser

object BodyParsers {

  def bytes: BodyParser[Array[Byte]] = BodyParser { _ =>
    Iteratee.consume[Array[Byte]]().map(Right.apply)
  }

}
