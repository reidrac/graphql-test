package graphql

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import dispatch._
import io.circe._, io.circe.parser._

class QueryFailed(message: String) extends Exception(message)

class GraphQlClient(
    endpoint: String,
    headers: Map[String, Seq[String]],
    timeout: Duration = 10.seconds
)(
    implicit ec: ExecutionContext = ExecutionContext.global,
    implicit val contextShift: ContextShift[IO]
) {

  lazy val http: Http = Http.withConfiguration(
    _.setRequestTimeout(timeout.toMillis.toInt)
      .setConnectTimeout(timeout.toMillis.toInt)
  )

  def exec(query: Json): IO[Json] =
    for {
      res <- IO.fromFuture(IO {
        http(
          url(endpoint).POST
            .setHeaders(
              headers ++ Map(
                "Accept" -> Seq("application/json"),
                "Content-Type" -> Seq("application/json; charset=utf-8")
              )
            )
            .setBody(query.noSpaces)
        )
      })
      json <- res match {
        case res if res.getStatusCode / 100 == 2 =>
          IO.fromEither(parse(res.getResponseBody()))
        case err => IO.raiseError(new QueryFailed(err.toString))
      }
    } yield json

  def shutdown: Unit = http.shutdown()
}
