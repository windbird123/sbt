name := "sbt"
organization in ThisBuild := "com.github.windbird123"
scalaVersion in ThisBuild := "2.11.12"

// PROJECTS
lazy val global = project
  .in(file("."))
  .settings(settings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    common,
    multi1,
    multi2
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )
  .disablePlugins(AssemblyPlugin)

lazy val multi1 = project
  .settings(
    name := "multi1",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.zio,
      dependencies.zioStreams
    )
  )
  .dependsOn(
    common
  )

lazy val multi2 = project
  .settings(
    name := "multi2",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.zio
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES
lazy val dependencies =
  new {
    val logbackV        = "1.2.3"
    val scalaLoggingV   = "3.9.2"
    val slf4jV          = "1.7.26"
    val typesafeConfigV = "1.3.2"
    val scalatestV      = "3.0.5"
    val zioV            = "1.0.0-RC18-2"

    // common dependencies
    val logback        = "ch.qos.logback"             % "logback-classic" % logbackV
    val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"  % scalaLoggingV
    val slf4j          = "org.slf4j"                  % "jcl-over-slf4j"  % slf4jV
    val typesafeConfig = "com.typesafe"               % "config"          % typesafeConfigV
    val zio            = "dev.zio"                    %% "zio"            % zioV
    val zioStreams     = "dev.zio"                    %% "zio-streams"    % zioV

    val zioTest    = "dev.zio"       %% "zio-test"     % zioV
    val zioTestSbt = "dev.zio"       %% "zio-test-sbt" % zioV
    val scalatest  = "org.scalatest" %% "scalatest"    % scalatestV

    // project specific dependencies
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

// SETTINGS
lazy val settings = commonSettings ++ wartremoverSettings ++ scalafmtSettings

lazy val compilerOptions = Seq(
  "-encoding",
  "UTF-8",                 // source files are in UTF-8
  "-deprecation",          // warn about use of deprecated APIs
  "-unchecked",            // warn about unchecked type parameters
  "-feature",              // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint",                // enable handy linter warnings
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

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
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

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
