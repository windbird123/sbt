import microsites._

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
    docs,
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

lazy val docs = project
  .settings(
    name := "docs",
    version := "1.0.0-SNAPSHOT",
    commonSettings,
    assemblySettings,
    docSettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.scalajHttp,
      dependencies.playJson
    )
  )
    .enablePlugins(MicrositesPlugin)
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
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.9"
    ),
    libraryDependencies ++= commonDependencies ++ sparkDependencies ++ Seq(
      dependencies.scalajHttp,
      dependencies.playJson,
      dependencies.elasticsearchHadoop
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
    val zio            = "dev.zio"                    %% "zio"            % "1.0.0-RC20"
    val zioStreams     = "dev.zio"                    %% "zio-streams"    % "1.0.0-RC20"
    val zioTest        = "dev.zio"                    %% "zio-test"       % "1.0.0-RC20" % "test"
    val zioTestSbt     = "dev.zio"                    %% "zio-test-sbt"   % "1.0.0-RC20" % "test"
    val scalatest      = "org.scalatest"              %% "scalatest"      % "3.0.5" % "test"

    // spark
    val sparkSql           = "org.apache.spark"             %% "spark-sql"            % "2.3.2"        % "provided"
    val sparkStreaming     = "org.apache.spark"             %% "spark-streaming"      % "2.3.2"        % "provided"
    val sparkTestingBase   = "com.holdenkarau"              %% "spark-testing-base"   % "2.3.2_0.14.0" % "test"
    val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.9"        % "test"

    // project specific dependencies
    val scalajHttp          = "org.scalaj"        %% "scalaj-http"         % "2.4.2"
    val playJson            = "com.typesafe.play" %% "play-json"           % "2.6.6"
    val elasticsearchHadoop = "org.elasticsearch" % "elasticsearch-hadoop" % "7.3.2"
  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.scalaLogging,
  dependencies.slf4j,
  dependencies.typesafeConfig,
  dependencies.zio,
  dependencies.zioStreams,
  dependencies.zioTest,
  dependencies.zioTestSbt,
  dependencies.scalatest
)

lazy val sparkDependencies = Seq(
  dependencies.sparkSql,
  dependencies.sparkStreaming,
  dependencies.sparkTestingBase,
  dependencies.jacksonModuleScala
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
  "-Ypartial-unification", // allow the compiler to unify type constructors of different arities
  "-language:implicitConversions"
)

lazy val commonSettings = Seq(
  scalacOptions := compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val assemblySettings = Seq(
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true),
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



lazy val docSettings = Seq(
    micrositeName := "Cats",
    micrositeDescription := "Lightweight, modular, and extensible library for functional programming",
    micrositeAuthor := "Cats contributors",
    micrositeFooterText := Some(
        """
          |<p>© 2020 <a href="https://github.com/typelevel/cats#maintainers">The Cats Maintainers</a></p>
          |<p style="font-size: 80%; margin-top: 10px">Website built with <a href="https://47deg.github.io/sbt-microsites/">sbt-microsites © 2020 47 Degrees</a></p>
          |""".stripMargin
    ),
    micrositeHighlightTheme := "atom-one-light",
    micrositeHomepage := "http://typelevel.org/cats/",
    micrositeBaseUrl := "cats",
    micrositeDocumentationUrl := "/cats/api/cats/index.html",
    micrositeDocumentationLabelDescription := "API Documentation",
    micrositeGithubOwner := "typelevel",
    micrositeExtraMdFilesOutput := resourceManaged.value / "main" / "jekyll",
    micrositeExtraMdFiles := Map(
        file("CONTRIBUTING.md") -> ExtraMdFileConfig(
            "contributing.md",
            "home",
            Map("title" -> "Contributing", "section" -> "contributing", "position" -> "50")
        ),
        file("README.md") -> ExtraMdFileConfig(
            "index.md",
            "home",
            Map("title" -> "Home", "section" -> "home", "position" -> "0")
        )
    ),
    micrositeGithubRepo := "cats",
    micrositeTheme := "pattern",
    micrositePalette := Map(
        "brand-primary" -> "#5B5988",
        "brand-secondary" -> "#292E53",
        "brand-tertiary" -> "#222749",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
    ),
    micrositeCompilingDocsTool := WithMdoc,
    autoAPIMappings := true,
    ghpagesNoJekyll := false,
    fork in mdoc := true,
    fork in (ScalaUnidoc, unidoc) := true,
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
        "-Xfatal-warnings",
        "-groups",
        "-doc-source-url",
        scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
        "-sourcepath",
        baseDirectory.in(LocalRootProject).value.getAbsolutePath,
        "-diagrams"
    ) ,
    scalacOptions ~= (_.filterNot(
        Set("-Ywarn-unused-import", "-Ywarn-unused:imports", "-Ywarn-dead-code", "-Xfatal-warnings")
    )),
    git.remoteRepo := "git@github.com:typelevel/cats.git",
    includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.svg",
    includeFilter in Jekyll := (includeFilter in makeSite).value,
    mdocIn := baseDirectory.in(LocalRootProject).value / "docs" / "src" / "main" / "mdoc",
    mdocExtraArguments := Seq("--no-link-hygiene")
)