addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")

libraryDependencies ++= Seq(
  "com.github.pauldraper.playclosure" % "plovr" % "0.0.9b267181"
)

name := "sbt-plugin"

organization := "com.github.pauldraper.playclosure"

sbtPlugin := true

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)

scalaVersion := "2.10.4"

version := "0.0-SNAPSHOT"
