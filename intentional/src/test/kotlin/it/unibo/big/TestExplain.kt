package it.unibo.big

import it.unibo.Intention
import it.unibo.describe.Scalability
import it.unibo.explain.ExplainExecute
import krangl.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class TestExplain {

    val path = "resources/intention/output/"

    fun execute(i: String) {
        try {
            val d = ExplainExecute.parse(i)
            val ret = ExplainExecute.execute(d, path)
            assertTrue(ret.second.nrow > 0)
            assertTrue(ret.third.nrow > 0)
        } catch (e: Exception) {
            e.printStackTrace()
            fail<String>(e.message)
        }
    }

    @BeforeEach
    fun before() {
        Intention.DEBUG = true
    }

    @Test
    fun `test cubes`() {
        execute("with ft_sales explain quantity by the_month")
        execute("with ft_salpurch explain unitcost by the_month")
    }

    @Test
    fun `test cube content`() {
        execute("with sales_fact_1997 explain unit_sales by the_month")
    }

    @Test
    fun `test models`() {
        execute("with sales_fact_1997 explain unit_sales by the_month using Polyfit")
        execute("with sales_fact_1997 explain unit_sales by the_month using CrossCorrelation")
        execute("with sales_fact_1997 explain unit_sales by the_month using Multireg")
        execute("with sales_fact_1997 explain unit_sales by the_month using Multireg, Polyfit")
    }

    @Test
    fun testScalability() {
        val writer = Files.newBufferedWriter(Paths.get("resources/intention/explain_time.csv"))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
        var first = true

        for (t in 0..5) {
            listOf(
                    "with sales explain unit_sales by product_family, the_month", // 36
                    "with sales explain unit_sales by the_date", // 323
                    "with sales explain unit_sales by product_category, the_month", // 540
                    "with sales explain unit_sales by product_subcategory, the_month", // 1224
                    "with sales explain unit_sales by product_category, the_date", // 12113
                    "with sales explain unit_sales by customer_id, the_month", // 16949
                    "with sales explain unit_sales by product_id, the_month",// 18492
                    "with sales explain unit_sales by the_date, customer_id", // 20k
                    "with sales explain unit_sales by the_date, product_id", // 77k
                    "with sales explain unit_sales by the_date, customer_id, product_id" // 87k
            ).forEach { s ->
                val d = ExplainExecute.parse(s)
                ExplainExecute.execute(d, path)
                if (first) csvPrinter.printRecord(d.statistics.keys.sorted())
                first = false
                csvPrinter.printRecord(d.statistics.keys.sorted().map { d.statistics[it] })
                csvPrinter.flush()
            }
        }
    }
}