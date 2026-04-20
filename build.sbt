import sbt.Keys.libraryDependencies

ThisBuild / organization := "com.alextesta"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.8.3"

ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions += "-Wunused:imports"

lazy val root = (project in file("."))
  .settings(
    name := "declerative-slides",

    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "mainargs"  % "0.7.8",
      "com.lihaoyi" %% "os-lib"    % "0.11.8",
      "org.scalatest" %% "scalatest" % "3.2.20" % Test
    ),

    Compile / mainClass := Some("declslides.cli.DeclSlidesCli")
  )