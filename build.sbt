val CucumberVersion = "2.0.0"

lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
lazy val cucumberCore = "io.cucumber" % "cucumber-core" % CucumberVersion % Test
lazy val cucumberScala = "io.cucumber" %% "cucumber-scala" % CucumberVersion % Test
lazy val cucumberJvm = "io.cucumber" % "cucumber-jvm" % CucumberVersion % Test
lazy val cucumberJunit = "io.cucumber" % "cucumber-junit" % CucumberVersion % Test
lazy val cucumberRunner = "com.waioeka.sbt" %% "cucumber-runner" % "0.1.3" % Test
lazy val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

val cucumberFramework = new TestFramework("com.waioeka.sbt.runner.CucumberFramework")
testFrameworks += cucumberFramework

lazy val `money-transfer-with-play-akka-wiremock` = (project in file(".")).enablePlugins(PlayScala)

name := "money-transfer-with-play-akka-wiremock"
version := "1.0"
scalaVersion := "2.12.3"

libraryDependencies ++= Seq(macwire, scalaTest, cucumberCore, cucumberScala, cucumberJvm, cucumberRunner, scalaTestPlay)

testOptions in Test += Tests.Argument(cucumberFramework, "--glue", "")
testOptions in Test += Tests.Argument(cucumberFramework, "--plugin", "pretty")
testOptions in Test += Tests.Argument(cucumberFramework, "--plugin", "html:/tmp/html")
