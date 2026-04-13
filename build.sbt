import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

ThisBuild / semanticdbEnabled := true

ThisBuild / scalacOptions += "-Wunused:imports"

lazy val root = (project in file("."))
  .settings(
    name := "declerative-slides",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.github.japgolly.scalacss" %% "core" % "1.0.0"
    )
  )
