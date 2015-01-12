package com.github.pauldraper.playclosure.play.iteratee

import java.io._
import play.api.libs.iteratee._
import scala.concurrent.ExecutionContext

object Enumerators {

  def outputStream(buffer: Int = 8 * 1024)(implicit ec: ExecutionContext): (OutputStream, Enumerator[Array[Byte]]) = {
    val inputStream = new PipedInputStream(buffer)
    val outputStream = new PipedOutputStream(inputStream)
    val enumerator = Enumerator.fromStream(inputStream, buffer)
    (outputStream, enumerator)
  }

}
