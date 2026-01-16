import Dependencies._

ThisBuild / scalaVersion     := "2.12.21"
ThisBuild / organization     := "com.cloud-apim"
ThisBuild / organizationName := "Cloud-APIM"
ThisBuild / description := "SecLang Engine Coreruleset is a Scala library meant to provide the OWASP Core Rule Set (CRS) in an easy and consumable way to be embedded in a Scala application."
ThisBuild / homepage := Some(url("https://github.com/cloud-apim/seclang-engine-coreruleset"))
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / developers := List(
  Developer(
    "mathieuancelin",
    "Mathieu ANCELIN",
    "mathieu@cloud-apim.com",
    url("https://github.com/mathieuancelin")
  ),
  Developer(
    "cloud-apim",
    "Cloud-APIM Team",
    "contact@cloud-apim.com",
    url("https://github.com/cloud-apim")
  )
)
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/cloud-apim/seclang-engine-coreruleset"),
    "scm:git@github.com:cloud-apim/seclang-engine-coreruleset.git"
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

usePgpKeyHex("235E536BA3E43419FD649B903C82DD5C11569EF6")

lazy val root = (project in file("."))
  .settings(
    name := "seclang-engine-coreruleset",
    libraryDependencies ++= Seq(
      "com.cloud-apim" %% "seclang-engine" % "1.2.0",
      munit % Test
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "SecLang Engine Coreruleset",
      "-doc-version", version.value
    )
  )
