import java.net.URL

import _root_.sbt.Keys._
import _root_.sbt._

object BuildSetting {

  val basicSettings = Seq(
    version := "0.0.1",
    homepage := Some(new URL("http://github.com/yangbajing/wechat-meal")),
    organization := "me.yangbajing",
    organizationHomepage := Some(new URL("https://github.com/yangbajing/wechat-meal")),
    startYear := Some(2015),
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq(
      "-encoding", "utf8",
      "-unchecked",
      "-feature",
      "-deprecation"
    ),
    javacOptions ++= Seq(
      "-encoding", "utf8",
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    ),
    publish :=(),
    publishLocal :=(),
    publishTo := None,
    offline := true,
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
      "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots")
  )

  val _scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.4"

  val _scalaLogging = ("com.typesafe.scala-logging" %% "scala-logging" % "3.1.0").exclude("org.scala-lang", "scala-library").exclude("org.scala-lang", "scala-reflect")
  val _typesafeConfig = "com.typesafe" % "config" % "1.3.0"

  val verAkkaHttp = "1.0"
  val _akkaStream = "com.typesafe.akka" %% "akka-stream-experimental" % verAkkaHttp
  val _akkaHttpCore = "com.typesafe.akka" %% "akka-http-core-experimental" % verAkkaHttp
  val _akkaHttp = "com.typesafe.akka" %% "akka-http-experimental" % verAkkaHttp
  val _akkaHttpPlayJson = "de.heikoseeberger" %% "akka-http-play-json" % "1.0.0"

  val verAkka = "2.3.12"
  val _akkaActor = "com.typesafe.akka" %% "akka-actor" % verAkka
  val _akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % verAkka

  val verPlay = "2.4.2"
  val _playJson = "com.typesafe.play" %% "play-json" % verPlay % "provided"
  val _play = ("com.typesafe.play" %% "play" % verPlay % "provided").
    exclude("com.typesafe.akka", "akka-actor").
    exclude("com.typesafe.akka", "akka-slf4j").
    exclude("org.scala-lang", "scala-library").
    exclude("org.scala-lang", "scala-compiler").
    exclude("org.scala-lang", "scala-reflect").
    exclude("org.scala-lang.modules", "scala-xml").
    exclude("org.scala-lang.modules", "scala-parser-combinators")

  val _slick = "com.typesafe.slick" %% "slick" % "3.0.1"
  val _slickPg = ("com.github.tminglei" %% "slick-pg" % "0.9.2").exclude("com.typesafe.slick", "slick")

  val _logback = "ch.qos.logback" % "logback-classic" % "1.1.3"
  val _commonsEmail = "org.apache.commons" % "commons-email" % "1.4"
  val _reactivemongo = "org.reactivemongo" %% "reactivemongo" % "0.11.5"
  val _guava = "com.google.guava" % "guava" % "18.0"
  val _postgresql = "org.postgresql" % "postgresql" % "9.4-1201-jdbc41"

  val _scalatestPlay = "org.scalatestplus" %% "play" % "1.4.0-M4" % "test"
  val _scalatest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
}