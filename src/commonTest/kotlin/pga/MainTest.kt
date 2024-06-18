package pga

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MainTest {
  @Test
  fun testParsing() {
    val line = "      - uses: actions/checkout@v3 # pga-ignore"
    assertEquals(
      line,
      processLine(line, 0, ::pinCallback)
    )
  }

  @Test
  fun getVersion() {
    getShaFromRef("gradle", "actions/setup-gradle", "tags/v3").also {
      println(it)
    }
    getShaFromRef("martinbonnin", "run-benchmarks", "heads/main").also {
      println(it)
    }
  }

  @Test
  fun regex() {
    REGEX.matchEntire("        uses: martinbonnin/run-benchmarks@main").let {
      assertNotNull(it)
    }
    REGEX.matchEntire("        uses: gradle/actions/setup-gradle@v3").let {
      assertNotNull(it)
    }
  }
}