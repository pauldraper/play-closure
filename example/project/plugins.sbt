addSbtPlugin("com.github.pauldraper.playclosure" %% "sbt-plugin" % "0.0-SNAPSHOT")

libraryDependencies ~= (_.map(_.exclude("org.sonatype.sisu", "sisu-guice")))
