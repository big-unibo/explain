package it.unibo.big

import it.unibo.Intention
import it.unibo.explain.ExplainExecute
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestExplain {

    val path = "resources/intention/output/"

    @BeforeEach
    fun before() {
        Intention.DEBUG = true
    }

    @Test
    fun `test cube content`() {
        try {
            val d = ExplainExecute.parse("with sales_fact_1997 explain unit_sales by the_month")
            val ret = ExplainExecute.execute(d, path)
            assertTrue(ret.second.nrow > 0)
            assertTrue(ret.third.nrow > 0)
        } catch (e: Exception) {
            e.printStackTrace()
            fail<String>(e.message)
        }
    }
}