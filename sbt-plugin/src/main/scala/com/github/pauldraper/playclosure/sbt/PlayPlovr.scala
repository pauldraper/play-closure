package com.github.pauldraper.playclosure.sbt

import org.plovr._
import play.PlayImport._
import sbt._
import sbt.Keys._
import scala.util.Try

object PlayPlovr extends AutoPlugin {

  object Keys {
    val plovrCompile = TaskKey[Unit]("plovrCompile", "Compile JS files")
    val plovrConfigDirectory = SettingKey[File]("plovrConfigDirectory", "Directory of Plovr configuration files")
    val plovrConfigs = SettingKey[Seq[File]]("plovrConfigs")
    val plovrJsTargetDirectory = SettingKey[File]("plovrJsTargetDirectory", "Directory of compiled JS files")
  }

  val autoImport = Keys
  import autoImport._

  override lazy val projectSettings = Seq(
    libraryDependencies += "com.github.pauldraper.playclosure" %% "play-plovr" % "0.0-SNAPSHOT" exclude("org.jboss.netty", "netty"),
    javaOptions in Runtime <+= plovrConfigs.map(c => "-Dplovr.config=" + c.map(_.absolutePath).mkString(",")),
    packageBin in Compile <<= (packageBin in Compile).dependsOn(plovrCompile),
    plovrCompile <<= (
        baseDirectory,
        plovrConfigDirectory,
        plovrConfigs,
        plovrJsTargetDirectory,
        streams
      ).flatMap { (baseDirectory, configDirectory, configs, targetDirectory, streams) =>
      streams.log.info(s"Compiling ${configs.size} Plovr targets")
      task {}.dependsOn({ // TODO: more idiomatic way to do this?
        configs.map { file =>
          task {
            val start = System.currentTimeMillis
            Try {
              ConfigParser.createBuilderFromFile(file)
            }.map { builder =>
              builder.setOutputFile(builder.build.getOutputFile)
              val config = builder.build
              val code = try {
                CompileRequestHandler.compile(config).getCompiledCode
              } catch {
                case e: Exception => throw new Exception(s"Could not compile ${config.getId}: ${e.getLocalizedMessage}", e)
              }

              val output = Option(config.getOutputFile).map { file =>
                rebase(configDirectory, targetDirectory)(file).get
              }.getOrElse {
                targetDirectory / s"${config.getId}.js"
              }
              IO.write(output, code, Option(config.getOutputCharset).getOrElse(IO.defaultCharset))

              val end = System.currentTimeMillis
              streams.log.info(f"Compiled ${config.getId} to ${Path.relativeTo(baseDirectory)(output).get} (${(end - start) / 1000d}%.1fs)")
              output
            }.recover {
              case e: Exception => throw new Exception(s"Could not parse config file ${file.name}: ${e.getLocalizedMessage}", e)
            }.get
          }
        }
      }: _*)
    },
    plovrConfigDirectory <<= PlayKeys.confDirectory(_ / "plovr"),
    plovrConfigs <<= plovrConfigDirectory(_ * "*.json")(_.get),
    plovrJsTargetDirectory <<= baseDirectory(_ / "public" / "javascripts" / "plovr")
  )

}
