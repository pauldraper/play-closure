package com.github.pauldraper.playclosure.play.controllers

import com.github.pauldraper.playclosure.play.http.PlayHttpExchange
import com.github.pauldraper.playclosure.play.logging.Logging
import com.google.common.base.Strings
import org.plovr.{ConfigParser, Config, CompilationServer, Handler}
import play.api.mvc._
import java.io.File

/**
 * @example
 *   GET  /plovr/&#47;path  com.github.pauldraper.playclosure.play.controllers.PlovrController(path)
 */
object PlovrController extends Controller with Logging {

  private val server = {
    val server = new CompilationServer("", 0, false)
    Option(System.getProperty("plovr.config")).map { property =>
      val paths = property.split(",")
      logger.debug(s"Configuring plovr with ${paths.length} config files")
      paths.foreach { path =>
        server.registerConfig(ConfigParser.parseFile(new File(path)))
      }
    }.getOrElse {
      logger.warn("No config specified for plovr")
    }
    server
  }

  def apply(path: String) = Action(BodyParsers.bytes) { request =>
    val plovrPath = s"/$path"
    val handler = Handler.values.maxBy { handler =>
      Strings.commonPrefix(plovrPath, handler.getContext).length
    }
    val result = if(plovrPath.startsWith(handler.getContext)) {
      val exchange = PlayHttpExchange(request)
      handler.createHandlerForCompilationServer(server).handle(exchange)
      exchange.result
    } else {
      None
    }
    result.getOrElse {
      NotFound("Not found")
    }
  }

}
