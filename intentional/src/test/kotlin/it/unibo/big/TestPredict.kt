package it.unibo.big

import it.unibo.Intention
import it.unibo.predict.PredictExecute
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class TestPredict {

    val path = "resources/intention/output/"

    fun execute(i: String) {
        try {
            val d = PredictExecute.parse(i)
            val ret = PredictExecute.execute(d, path)
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
    fun `test cimice 4`() {
        execute("with CIMICE predict adults for month between ['2021-05', '2021-09'] and province in ('BO', 'RA') by week, province from small_instars, total_captures")
    }

    @Test
    fun `test cimice 3`() {
        execute("with CIMICE predict adults for month between ['2021-05', '2021-09'] by week, province from small_instars, total_captures")
        // execute("with CIMICE predict adults for month between ['2021-05', '2021-09'] by week, crop_type from small_instars, total_captures")
    }

    @Test
    fun `test cimice2`() {
        execute("with CIMICE predict adults for month between ['2021-05', '2021-09'] by week from small_instars, total_captures")
    }

    @Test
    fun `test cimice 1`() {
        execute("with CIMICE predict adults by week")
    }

    @Test
    fun `test watering`() {
        execute("with WATERING predict value by day, field")
    }

    @Test
    fun `test models`() {
        execute("with sales_fact_1997 predict unit_sales by the_date")
    }

    @Test
    fun `test models 2`() {
        execute("with sales_fact_1997 predict unit_sales by the_date using univariateTS")
        execute("with sales_fact_1997 predict unit_sales by the_month using multivariateTS")
        execute("with sales_fact_1997 predict unit_sales by the_month using timeDecisionTree")
        execute("with sales_fact_1997 predict unit_sales by the_month using timeRandomForest, univariateTS")
    }

    @Test
    fun testScalability() {
        val writer = Files.newBufferedWriter(Paths.get("resources/intention/predict_time.csv"))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
        var first = true

        for (t in 0..9) {
            listOf(
                    "with sales predict unit_sales by the_month", // 12
                    "with sales predict unit_sales by product_family, the_month", // 36
                    "with sales predict unit_sales by the_date", // 323
                    "with sales predict unit_sales by product_category, the_month", // 540
                    "with sales predict unit_sales by product_subcategory, the_month", // 1224
                    "with sales predict unit_sales by product_category, the_date", // 12113
                    "with sales predict unit_sales by customer_id, the_month", // 16949
                    "with sales predict unit_sales by product_id, the_month", // 18492
                    "with sales predict unit_sales by the_date, customer_id", // 20k
                    "with sales predict unit_sales by the_date, product_id", // 77k
                    "with sales predict unit_sales by the_date, customer_id, product_id" // 87k
            ).forEach { s ->
                val d = PredictExecute.parse(s)
                PredictExecute.execute(d, path)
                if (first) csvPrinter.printRecord(d.statistics.keys.sorted())
                first = false
                csvPrinter.printRecord(d.statistics.keys.sorted().map { d.statistics[it] })
                csvPrinter.flush()
            }
        }
    }
}