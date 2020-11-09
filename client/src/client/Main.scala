package client

import org.slf4j.LoggerFactory
import cats.effect._
import io.circe._, io.circe.generic.auto._, io.circe.syntax._

import graphql._, GraphQlQuery._

// some case classes to process the responses
case class Info(
    count: Int
)

case class CharactersResults(
    name: String,
    gender: String
)

case class Characters(
    info: Info,
    results: Seq[CharactersResults]
)

case class Data(
    characters: Characters
)

// used to set the variables in one of the API calls
case class Variables(
    name: String
)

object Main extends IOApp {

  val logger = LoggerFactory.getLogger(getClass())

  def run(args: List[String]): IO[ExitCode] = {

    implicit val cli = new GraphQlClient(
      "https://rickandmortyapi.com/graphql"
    )

    // simple query
    val charQuery = GraphQlQuery("""query {
        |  characters(page: 1) {
        |    info {
        |      count
        |    }
        |    results {
        |      name
        |      gender
        |    }
        |  }
        |}""".stripMargin).asJson

    // query with variables
    val charVarQuery =
      GraphQlQuery("""query Chars($name: String) {
        |  characters(page: 1, filter: { name: $name }) {
        |    info {
        |      count
        |    }
        |    results {
        |      name
        |      gender
        |    }
        |  }
        |}""".stripMargin).asJson
        .deepMerge(Json.obj("variables" -> Variables(name = "rick").asJson))

    for {
      _ <- IO(logger.info("GraphQL starts"))

      // query the schema
      schema <- GraphQlQuery.getSchema.asJson.exec
      _ <- IO(logger.info(schema.toString()))

      // simple query
      data <- charQuery.asJson.execAs[Data]
      _ <- IO {
        logger.info(s"records: ${data.characters.info}")
        data.characters.results
          .foreach(char => logger.info(char.toString()))
      }

      // query with variables
      dataVar <- charVarQuery.execAs[Data]
      _ <- IO {
        logger.info(s"records: ${dataVar.characters.info}")
        dataVar.characters.results
          .foreach(char => logger.info(char.toString()))
      }

      _ <- IO(cli.shutdown)

      _ <- IO(logger.info("Done"))
    } yield ExitCode.Success
  }
}
