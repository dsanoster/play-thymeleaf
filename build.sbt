name := """play-thymeleaf"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

// Add app folder as resource directory so that mapper xml files are in the classpath
unmanagedResourceDirectories in Compile <+= baseDirectory( _ / "app" )

// but filter out java and html files that would then also be copied to the classpath
excludeFilter in Compile in unmanagedResources := "*.java" 

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "com.google.inject.extensions" % "guice-multibindings" % "4.0",
  "net.sourceforge.nekohtml" % "nekohtml" % "1.9.22",
  "org.thymeleaf" % "thymeleaf" % "2.1.4.RELEASE",
  "nz.net.ultraq.thymeleaf" % "thymeleaf-layout-dialect" % "1.1.3",
  "io.kamon" % "kamon-core_2.11" % "0.5.2",
  "io.kamon" % "kamon-play-24_2.11" % "0.5.2",
  "org.aspectj" % "aspectjweaver" % "1.8.7",
  "janino" % "janino" % "2.5.10"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

aspectjSettings

javacOptions := Seq("-source", "1.8", "-target", "1.8")

javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj

fork in run := true
