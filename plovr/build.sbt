import de.johoop.ant4sbt.Ant4Sbt._

val plovrDirectory = baseDirectory(_ / "plovr")

def rebaseDirectory(file: File, extra: File => File, prefix: String = "", pattern: String = "*") = extra(file) ** pattern pair rebase(file, prefix)

addAntTasks(
  "externs",
  "library",
  "pre-compile",
  "third_party"
)

antSettings

antBaseDir := plovrDirectory.value

antBuildFile := antBaseDir.value / "build.xml"

compile in Compile <<= (compile in Compile).dependsOn(
  antTaskKey("externs"),
  antTaskKey("library"),
  antTaskKey("pre-compile"),
  antTaskKey("third_party")
)

crossPaths := false

libraryDependencies ++= Seq(
  "com.google.closure-stylesheets" % "closure-stylesheets" % "20140426",
  "com.google.code.gson" % "gson" % "2.2.2",
  "com.google.inject" % "guice" % "4.0-beta5", // for templates
  "com.google.inject.extensions" % "guice-assistedinject" % "4.0-beta5", // for templates; 4.0-beta5 https://github.com/google/guice/issues/846
  "com.google.inject.extensions" % "guice-multibindings" % "4.0-beta5", // for templates
  "com.google.javascript" % "closure-compiler" % "v20141215",
//"com.google.template" % "soy" % "2012-12-21", // uncomment once Plovr is compatible with a published version
  "com.ibm.icu" % "icu4j" % "51.1", // for templates
  "org.seleniumhq.selenium" % "selenium-java" % "2.21.0"
)

mappings in (Compile, packageBin) ++=
  rebaseDirectory(plovrDirectory.value / "build" / "classes", identity, "", "*.txt") ++
  rebaseDirectory(plovrDirectory.value / "closure" / "closure-compiler" / "contrib" / "externs", identity, "contrib") ++
  rebaseDirectory(plovrDirectory.value / "closure" / "closure-compiler", _ / "externs") ++
  rebaseDirectory(plovrDirectory.value / "closure" / "closure-library" / "closure", _ / "goog", "closure") ++
  rebaseDirectory(plovrDirectory.value / "closure" / "closure-library", _ / "third_party") ++
  rebaseDirectory(plovrDirectory.value / "closure" / "closure-templates" / "javascript", identity, "soy", "*goog.js") ++
  rebaseDirectory(plovrDirectory.value / "src", identity, "", "*.soy") ++
  rebaseDirectory(plovrDirectory.value / "src", _ / "org" / "plovr", "", "*.js")

name := "plovr"

organization := "com.github.pauldraper.playclosure"

description := "A combined toolset and build server for Google Closure"

isSnapshot := true

unmanagedSourceDirectories in Compile ++= Seq(
  plovrDirectory.value / "closure" / "closure-stylesheets" / "build" / "genfiles" / "java",
  plovrDirectory.value / "closure" / "closure-templates" / "java" / "src",
  plovrDirectory.value / "closure" / "closure-templates" / "build" / "genfiles",
  plovrDirectory.value / "src"
)

version := "0.0." + "git --git-dir=plovr/.git rev-parse HEAD".!!.take(8)
