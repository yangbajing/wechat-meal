import _root_.sbt.Keys._
import _root_.sbt._
import com.thoughtworks.sbtApiMappings.ApiMappings
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.Play.autoImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys
import play.twirl.sbt.Import.TwirlKeys

object Build extends Build {

  import BuildSetting._

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
        cache,
        _play,
        _slick,
        _slickPg,
        _postgresql,
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

}

