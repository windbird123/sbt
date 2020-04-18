name := "sbt"
version := "1.0.0-SNAPSHOT"
organization in ThisBuild := "com.github.windbird123"
scalaVersion in ThisBuild := "2.11.12"

// PROJECTS
lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    common,
    multi1,
    spark
  )

lazy val common = project
  .settings(
    name := "common",
    version := "1.0.0-SNAPSHOT",
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .disablePlugins(AssemblyPlugin)

lazy val multi1 = project
  .settings(
    name := "multi1",
    version := "1.0.0-SNAPSHOT",
    commonSettings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.scalajHttp,
      dependencies.playJson
    )
  )
  .dependsOn(
    common
  )

lazy val spark = project
  .settings(
    name := "spark",
    version := "1.0.0-SNAPSHOT",
    commonSettings,
    assemblySettings,
    dependencyOverrides ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
    ),
    libraryDependencies ++= commonDependencies ++ sparkDependencies ++ Seq(
      dependencies.scalatest,
      dependencies.scalajHttp,
      dependencies.playJson
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES
lazy val dependencies =
  new {
    // common dependencies
    val logback        = "ch.qos.logback"             % "logback-classic" % "1.2.3"
    val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"
    val slf4j          = "org.slf4j"                  % "jcl-over-slf4j"  % "1.7.26"
    val typesafeConfig = "com.typesafe"               % "config"          % "1.3.2"
    val zio            = "dev.zio"                    %% "zio"            % "1.0.0-RC18-2"
    val zioStreams     = "dev.zio"                    %% "zio-streams"    % "1.0.0-RC18-2"
    val zioTest        = "dev.zio"                    %% "zio-test"       % "1.0.0-RC18-2"
    val zioTestSbt     = "dev.zio"                    %% "zio-test-sbt"   % "1.0.0-RC18-2"
    val scalatest      = "org.scalatest"              %% "scalatest"      % "3.0.5"

    // spark
    val sparkSql         = "org.apache.spark" %% "spark-sql"          % "2.0.2"
    val sparkStreaming   = "org.apache.spark" %% "spark-streaming"    % "2.0.2"
    val sparkTestingBase = "com.holdenkarau"  %% "spark-testing-base" % "2.0.2_0.10.0"

    // project specific dependencies
    val scalajHttp = "org.scalaj"        %% "scalaj-http" % "2.4.2"
    val playJson   = "com.typesafe.play" %% "play-json"   % "2.6.6"
  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.zio,
  dependencies.zioStreams,
  dependencies.zioTest    % "test",
  dependencies.zioTestSbt % "test",
  dependencies.scalatest  % "test"
)

lazy val sparkDependencies = Seq(
  dependencies.sparkSql         % "provided",
  dependencies.sparkStreaming   % "provided",
  dependencies.sparkTestingBase % "test"
)

// SETTINGS
lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8", // source files are in UTF-8
  "-target:jvm-1.8",
  "-deprecation",          // warn about use of deprecated APIs
  "-unchecked",            // warn about unchecked type parameters
  "-feature",              // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xfatal-warnings",      // turn compiler warnings into errors
  "-Ypartial-unification", // allow the compiler to unify type constructors of different arities
  "-language:implicitConversions"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val assemblySettings = Seq(
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "application.conf"            => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

addCompilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.3")
addCompilerPlugin("org.scalamacros" % "paradise"        % "2.1.0" cross CrossVersion.full)

// testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
