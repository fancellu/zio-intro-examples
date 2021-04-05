name := "zio-intro-examples"

version := "0.1"

scalaVersion := "2.13.5"

val zioVersion = "1.0.5"

libraryDependencies += "dev.zio" %% "zio" % zioVersion

libraryDependencies += "dev.zio" %% "zio-json" % "0.1.3"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"          % zioVersion,
  "dev.zio" %% "zio-test-sbt"      % zioVersion
).map(_%Test)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
