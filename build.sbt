name := "BlackJack"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.14"