import sbt.Keys._
import sbt.Project.projectToRef

name := """play_autoquery_template"""

version := "1.0-SNAPSHOT"

val projectNamespace = "org.example.project"

// in Global is necessary for child projects
scalaVersion in Global := "2.11.8"

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

val js_projects = Seq(js)

lazy val web = (project in file("web")).enablePlugins(PlayScala).settings(
  scalaJSProjects := js_projects,
  pipelineStages := Seq(scalaJSProd),
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    specs2 % Test
  ),
  unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
).aggregate((Seq(js).map(projectToRef)): _*)
  .dependsOn(shared_js_jvm, rest_models_jvm)

lazy val rest_models = (crossProject.crossType(CrossType.Pure) in file("rest_models")).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "upickle" % "0.4.1"
  )
)

lazy val rest_models_jvm = rest_models.jvm
lazy val rest_models_js = rest_models.js

lazy val shared_js = (crossProject.crossType(CrossType.Pure) in file("shared_js")).settings(
)

lazy val shared_js_jvm = shared_js.jvm
lazy val shared_js_js = shared_js.js

lazy val js = (project in file("js")).settings(
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  aggregate(shared_js_js).
  dependsOn(shared_js_js, rest_models_js)

lazy val api = (project in file("api")).enablePlugins(PlayScala).settings(
  libraryDependencies ++= Seq(
    jdbc,
    cache,
    ws,
    specs2 % Test,
    "org.squeryl" %% "squeryl" % "0.9.6-SNAPSHOT",
    "org.postgresql" % "postgresql" % "9.4-1202-jdbc4",
    "xyz.mattclifton" %% "autoschema" % "1.0",
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "xyz.mattclifton" %% "autoquery-components" % "0.1-SNAPSHOT",
    "com.mohiva" %% "play-silhouette" % "4.0.0-RC1",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-RC1",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-RC1",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0-RC1",
    "org.webjars" %% "webjars-play" % "2.5.0-2",
    "net.codingwell" %% "scala-guice" % "4.0.1",
    "com.iheart" %% "ficus" % "1.2.6",
    "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
  ),
  unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
).dependsOn(schema, squeryl_models, squeryl_queries, rest_models_jvm)

lazy val schema = Project(id = "schema", base = file("schema"))
  .enablePlugins(SbtSquerylAutoSchema)
  .settings(
    name := "schema",
    squerylBaseTypes := Seq("org.example.project.schema.Account", "org.example.project.schema.AccountProvider"),
    squerylSchemaName := "Database",
    squerylPackageName := s"${projectNamespace}.squeryl.models",
    squerylDirectoryPath := s"squeryl_models/src/main/scala/${projectNamespace.replace(".", "/")}/squeryl/models",
    libraryDependencies ++= Seq(
      "xyz.mattclifton" %% "autoschema" % "1.0",
      "xyz.mattclifton" %% "autoquery-components" % "0.1-SNAPSHOT"
    )
  )

lazy val squeryl_models = Project(id = "squeryl_models", base = file("squeryl_models"))
  .enablePlugins(SbtSquerylAutoQuery)
  .settings(
    name := "squeryl_models",
    squerylQueryTraits := Seq("org.example.project.schema.Query"),
    squerylQuerySchemaName := s"${projectNamespace}.squeryl.models.Database",
    squerylQuerySchemaBaseClass := s"${projectNamespace}.squeryl.models.DatabaseDefinition",
    squerylQuerySquerylName := s"${projectNamespace}.squeryl.models.Squeryl",
    squerylQueryPackageName := s"${projectNamespace}.squeryl.queries",
    squerylQueryDirectoryPath := s"squeryl_queries/src/main/scala/${projectNamespace.replace(".", "/")}/squeryl/queries",
    libraryDependencies ++= Seq(
      "xyz.mattclifton" %% "autoquery-components" % "0.1-SNAPSHOT",
      "org.squeryl" %% "squeryl" % "0.9.6-SNAPSHOT"
    )
  )
  .dependsOn(schema)

lazy val squeryl_queries = Project(id = "squeryl_queries", base = file("squeryl_queries"))
  .settings(
    name := "squeryl_queries",
    libraryDependencies ++= Seq(
      "xyz.mattclifton" %% "autoquery-components" % "0.1-SNAPSHOT"
    )
  )
  .dependsOn(schema, squeryl_models)
