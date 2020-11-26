// build.sc
import mill._, scalalib._, scalafmt._
import $ivy.`com.goyeau::mill-scalafix:0.1.5`
import com.goyeau.mill.scalafix.ScalafixModule

object client extends ScalaModule with ScalafmtModule with ScalafixModule {
  def scalaVersion = "2.13.3"
  def scalacOptions = Seq("-Wunused:imports")

  val circeVersion = "0.12.3"
  val dispatchhttpVersion = "1.2.0"
  val logbackVersion = "1.2.3"
  val catsEffectVersion = "2.2.0"

  def ivyDeps = Agg(
    ivy"io.circe::circe-core::$circeVersion",
    ivy"io.circe::circe-generic::$circeVersion",
    ivy"io.circe::circe-parser::$circeVersion",
    ivy"org.dispatchhttp::dispatch-core:$dispatchhttpVersion",
    ivy"ch.qos.logback:logback-classic:$logbackVersion",
    ivy"org.typelevel::cats-effect:$catsEffectVersion"
    )
}

