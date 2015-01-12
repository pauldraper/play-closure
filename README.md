# play-closure

The project brings the goodness of Google Closure and the features of Plovr into the Scala Play framework.

This is under active development.

## Usage

### Setup

(Artificats have not yet been publish...this only describes how it will work.)

1. In project/plugins.sbt, add

  ```scala
  addSbtPlugin("com.github.pauldraper.playclosure" % "sbt-plugin" % "0.0-SNAPSHOT")

  libraryDependencies ~= (_.map(_.exclude("org.sonatype.sisu", "sisu-guice")))
  ```

1. In build.sbt, enable the `PlayPlovr` plugin.

  ```scala
  lazy val example = (project in file(".")).enablePlugins(PlayScala, PlayPlovr)
  ```

1. Add a route for Plovr in conf/routes.

  ```
  GET /plovr/*path com.github.pauldraper.playclosure.play.controllers.PlovrController(path)
  ```

1. Put your Plovr configurations as .json files in conf/plovr.

### Deploying

Plovr will compile the JS and include them in the jar.
