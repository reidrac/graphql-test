# Playing with GraphQL from Scala

My experience with [GraphQL](https://graphql.org/) so far hasn't been very
good, so I decided to play a bit more with it without deadlines or having
to actually accomplish anything.

I'm using a free public API by [The Rick and Morty API](https://rickandmortyapi.com/).

At the end of the day, it is mostly JSON manipulation.

Using:
 - circe to manage JSON
 - dispatchhttp for requests
 - Cats Effect to wrap it all

Build with [Mill](http://www.lihaoyi.com/mill/).

    mill client.run

This code is public domain.

