package com.github.pauldraper.playclosure.play.http

import com.github.pauldraper.playclosure.play.iteratee.Enumerators
import com.sun.net.httpserver.{Headers, HttpExchange}
import java.io._
import java.net.{InetSocketAddress, URI}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.mvc.Results.Status
import play.mvc.Http.HeaderNames
import scala.collection.JavaConversions._

class PlayHttpExchange(request: Request[Array[Byte]]) extends HttpExchange {

  private val attributes = new java.util.HashMap[String, AnyRef]()

  private var responseCode = -1

  private val responseHeaders = new Headers

  private var responseLength = 0L

  private val (outputStream, enumerator) = Enumerators.outputStream()

  private lazy val result_ = {
    val headers = responseHeaders.toSeq.flatMap { case (key, values) =>
      values.map(key -> _)
    }
    val status = Status(responseCode)
    if (responseLength == -1) {
      status.chunked(enumerator >>> Enumerator.eof)
    } else if (responseLength == 0) {
      status
    } else {
      status.feed(enumerator >>> Enumerator.eof).withHeaders(HeaderNames.CONTENT_LENGTH -> responseLength.toString)
    }.withHeaders(headers: _*)
  }

  /**
   * Returns a Some with the corresponding Play [[Result]], or None if [[sendResponseHeaders]] has
   * not been called.
   */
  def result = {
    if (responseCode == -1) {
      None
    } else {
      Some(result_)
    }
  }

  def getRequestHeaders = {
    val headers = new Headers
    request.headers.toMap.foreach { case (key, values) =>
      headers.put(key, values)
    }
    headers
  }

  def getHttpContext = ???

  def getResponseCode = responseCode

  def getResponseHeaders = responseHeaders

  def getPrincipal = null

  def getAttribute(s: String) = attributes.get(s)

  def setStreams(inputStream: InputStream, outputStream: OutputStream): Unit = ???

  def getRequestURI = new URI(request.uri)

  def getLocalAddress = {
    val hostname = request.domain
    val port = request.host.split(":").lift(1).map(_.toInt).getOrElse(if(request.secure) 443 else 80)
    new InetSocketAddress(hostname, port)
  }

  def getProtocol: String = if(request.secure) "https" else "http"

  def setAttribute(s: String, o: AnyRef) = attributes.put(s, o)

  def getRemoteAddress: InetSocketAddress = ???

  def getRequestMethod = request.method

  def close() = outputStream.close()

  def getResponseBody = outputStream

  def sendResponseHeaders(i: Int, l: Long) = {
    responseCode = i
    responseLength = l
  }

  def getRequestBody = new ByteArrayInputStream(request.body)

}

object PlayHttpExchange {

  def apply(request: Request[Array[Byte]]) = new PlayHttpExchange(request)

}
