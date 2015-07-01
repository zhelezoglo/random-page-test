name := "random-page-test"

version := "1.0"

scalaVersion := "2.11.7"

testOptions in Test += Tests.Argument("-oD")

libraryDependencies ++=
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.3" ::
    "ch.qos.logback" % "logback-classic" % "1.1.2" ::
      "com.typesafe" % "config" % "1.3.0" ::
      Nil


libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"