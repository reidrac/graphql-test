package graphql

import cats.effect.IO
import io.circe._

case class GraphQlQuery(query: String)

object GraphQlQuery {

  val getSchema = GraphQlQuery("""query {
    __schema {
      types {
        name
        kind
        description
        fields {
          name
        }
      }
    }
  }""")

  implicit class GraphQlQueryJsonOps(query: Json)(implicit
      cli: GraphQlClient
  ) {
    def exec: IO[Json] = cli.exec(query)

    def execAs[T](implicit
        decoderT: io.circe.Decoder[T]
    ): IO[T] =
      for {
        json <- query.exec
        t <- IO.fromEither(json.hcursor.get[T]("data"))
      } yield t
  }
}
