package pga

import kotlin.test.Test
import kotlin.test.assertEquals

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
    val sha = getShaFromRef("martinbonnin", "run-benchmarks", "heads/main")
    println(sha)
  }

  @Test
  fun regex() {
    val result = REGEX.matchEntire("        uses: martinbonnin/run-benchmarks@main")
    check(result != null)
  }
}