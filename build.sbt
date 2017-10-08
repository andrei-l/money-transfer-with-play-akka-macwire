lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
lazy val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
lazy val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % Test

lazy val `money-transfer-with-play-akka-macwire` = (project in file(".")).enablePlugins(PlayScala)

name := "money-transfer-with-play-akka-macwire"
version := "1.0"
scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  macwire, scalaTest, scalaTestPlay, akkaTestKit
)
