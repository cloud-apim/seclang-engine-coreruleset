import Dependencies._

lazy val scala212 = "2.12.21"
lazy val scala213 = "2.13.18"
lazy val scala3   = "3.8.4"

ThisBuild / scalaVersion     := scala212
ThisBuild / crossScalaVersions := Seq(scala212, scala213, scala3)
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

// the jar embeds whatever setup.sh left in src/main/resources/crs, which is gitignored:
// refuse to package a tree that is missing, or that mixes files from several CRS versions
lazy val checkCrs = taskKey[Unit]("Checks that the embedded CRS is installed and comes from a single version")

lazy val root = (project in file("."))
  .settings(
    name := "seclang-engine-coreruleset",
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies ++= Seq(
      "com.cloud-apim" %% "seclang-engine" % "2.4.0",
      munit % Test
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "SecLang Engine Coreruleset",
      "-doc-version", version.value
    ),
    checkCrs := {
      val dir     = (Compile / resourceDirectory).value / "crs"
      val setup   = dir / "crs-setup.conf"
      val rules   = dir / "rules"
      val example = dir / "crs-setup.conf.example"
      def fail(msg: String): Nothing = throw new sbt.internal.util.MessageOnlyException(msg)
      if (!setup.isFile || !rules.isDirectory) fail(s"no CRS installed in $dir, run ./setup.sh first")
      if (example.exists) fail(s"$example would be shipped next to crs-setup.conf, run ./setup.sh again")
      val ver      = """ver:'OWASP_CRS/(\d+\.\d+\.\d+)'""".r
      val files    = setup +: (rules ** "*.conf").get
      val versions = files.flatMap(f => ver.findAllMatchIn(IO.read(f)).map(_.group(1))).distinct.sorted
      if (versions.size != 1) fail(s"the CRS in $dir mixes versions ${versions.mkString(", ")}, run ./setup.sh again")
      streams.value.log.info(s"embedded CRS ${versions.head}, ${files.size} files")
    },
    Compile / packageBin := (Compile / packageBin).dependsOn(checkCrs).value
  )
