import _root_.sbt.Keys._
import _root_.sbt._
import com.thoughtworks.sbtApiMappings.ApiMappings
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.Play.autoImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys
import play.twirl.sbt.Import.TwirlKeys

object Build extends Build {

  override lazy val settings = super.settings :+ {
    shellPrompt := (s => Project.extract(s).currentProject.id + " > ")
  }

  lazy val app = Project("wechat-meal", file("."))
    .enablePlugins(PlayScala)
    .enablePlugins(ApiMappings)
    .aggregate(platform, weixin)
    .dependsOn(platform, weixin)
    .settings(basicSettings)
    .settings(
      description := "Wechat Meal",
      PlayKeys.playDefaultPort := 2015,
      TwirlKeys.templateImports ++= Seq(
        "me.yangbajing.wechatmeal.common.enums._",
        "me.yangbajing.wechatmeal.data.model._",
        "me.yangbajing.wechatmeal.data.domain._"),
      RoutesKeys.routesGenerator := InjectedRoutesGenerator,
      libraryDependencies += _akkaHttp,
      libraryDependencies ~= {
        _ map {
          case m if m.organization == "com.typesafe.play" =>
            m.exclude("commons-logging", "commons-logging").
              exclude("com.typesafe.akka", "akka-actor").
              exclude("com.typesafe.akka", "akka-slf4j").
              exclude("org.scala-lang", "scala-library").
              exclude("org.scala-lang", "scala-compiler").
              exclude("org.scala-lang", "scala-reflect").
              exclude("org.scala-lang.modules", "scala-xml").
              exclude("org.scala-lang.modules", "scala-parser-combinators").
              excludeAll()
          case m => m
        }
      })

  lazy val platform = Project("wechat-meal-platform", file("platform"))
    .enablePlugins(ApiMappings)
    .dependsOn(weixin)
    .settings(basicSettings)
    .settings(
      description := "Wechat Meal",
      libraryDependencies ++= Seq(
        ws,
        _play,
        _akkaStream,
        _scalaLogging,
        _scalaXml,
        _scalatest))

  lazy val weixin = Project("weixin", file("weixin"))
    .enablePlugins(ApiMappings)
    .settings(basicSettings)
    .settings(
      description := "weixin",
      libraryDependencies ++= Seq(
        ws % "provided",
        _playJson,
        _akkaHttp % "provided",
        _scalaLogging % "provided",
        _scalaXml % "provided",
        _scalatest))

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

  val _logback = "ch.qos.logback" % "logback-classic" % "1.1.3"
  val _commonsEmail = "org.apache.commons" % "commons-email" % "1.4"
  val _reactivemongo = "org.reactivemongo" %% "reactivemongo" % "0.11.5"
  val _guava = "com.google.guava" % "guava" % "18.0"

  val _scalatestPlay = "org.scalatestplus" %% "play" % "1.4.0-M4" % "test"
  val _scalatest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
}

