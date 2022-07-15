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
}