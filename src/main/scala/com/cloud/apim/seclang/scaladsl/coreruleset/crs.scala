package com.cloud.apim.seclang.scaladsl.coreruleset

import com.cloud.apim.seclang.model._

object EmbeddedCRSPreset {
  val embedded: SecLangPreset = {
    SecLangPreset.fromSource(
      name = "crs",
      rulesSource = ConfigurationSourceList(List(
        // anchored: the scan is a substring search, so `.*\.conf` alone also loads crs-setup.conf.example
        ResourceScanConfigurationSource("crs", """.*\.conf$""")
      )),
      filesSource = FilesSourceList(List(
        ResourceScanFilesSource("crs", """.*\.data$""")
      ))
    )
  }
}