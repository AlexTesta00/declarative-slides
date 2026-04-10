import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

ThisBuild / semanticdbEnabled := true

ThisBuild / scalacOptions += "-Wunused:imports"

lazy val root = (project in file("."))
  .settings(
    name := "declerative-slides",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test
  )
