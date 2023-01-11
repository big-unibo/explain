package it.unibo.big

import it.unibo.assessext.AssessExecuteExt
import it.unibo.assessext.AssessExt
import it.unibo.conversational.datatypes.DependencyGraph
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*
import io.github.cdimascio.dotenv.Dotenv
import it.w4bo.database.waitForIt

class TestAssessExt {

    companion object {
        private const val path = "resources/intention/output/"
        val dotenv = Dotenv.load()
        val WAIT: Int = 1000 * 60 * 1

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            try {
                waitForIt(dotenv.get("ORACLE_DBMS"), dotenv.get("ORACLE_IP"), dotenv.get("ORACLE_PORT").toInt(), dotenv.get("ORACLE_DB"), "foodmart", dotenv.get("ORACLE_PWD"), WAIT)
                waitForIt(dotenv.get("ORACLE_DBMS"), dotenv.get("ORACLE_IP"), dotenv.get("ORACLE_PORT").toInt(), dotenv.get("ORACLE_DB"), "covid_weekly", dotenv.get("ORACLE_PWD"), WAIT)
                waitForIt(dotenv.get("ORACLE_DBMS"), dotenv.get("ORACLE_IP"), dotenv.get("ORACLE_PORT").toInt(), dotenv.get("ORACLE_DB"), "ssb_flight", dotenv.get("ORACLE_PWD"), WAIT)
            } catch (e: Exception) {
                fail { e.message!! }
            }
        }
    }

//    @Test
//    fun testRefinement1() { // TODO: uncomment this to support refinement
//        try {
//            val c: AssessExt = AssessExecuteExt.parse("with consommation_electrique assess consototale by secteurnaf2")
//            AssessExecuteExt.execute(c, path)
//            val d = c.partialRefinements[0]
//            AssessExecuteExt.execute(d, path)
//            val a: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, category")
//            AssessExecuteExt.execute(a, path)
//            val b = a.partialRefinements[0]
//            AssessExecuteExt.execute(b, path)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            fail(e.message)
//        }
//    }

    @Test
    fun testMeasure() {
        try {
            AssessExecuteExt.parse("with covid19 assess cases, deaths by country against continent")
            fail("This should fail, assess does not accept two measures")
        } catch (e: Exception) {
            e.message?.let { assertTrue(it.isNotEmpty()) }
        }
    }

    @Test
    fun testParents() {
        try {
            val a: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation")
            AssessExecuteExt.execute(a, path)
            // val b: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, brand")
            // AssessExecuteExt.execute(b, path)
            val c: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, category")
            AssessExecuteExt.execute(c, path)
            val g: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against region scaled population using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(g, path)
            val j: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against region using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(j, path)
            val d: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by category")
            AssessExecuteExt.execute(d, path)
            val e: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by category against allproduct")
            AssessExecuteExt.execute(e, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testFailure1() {
        try {
            val f: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation for nation = 'GERMANY' against region = 'FRANCE' using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(f, path)
            fail()
        } catch (e: Exception) {
            assertTrue(e.message!!.isNotEmpty())
        }
    }

    @Test
    fun testFailure2() {
        try {
            val f: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against category using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(f, path)
            fail()
        } catch (e: Exception) {
            assertTrue(e.message!!.isNotEmpty())
        }
    }

//    @Test
//    fun testScalability() {
//        fun refine(a: AssessExt, first: Boolean = true) {
//            if (first) {
//                AssessExt.curId = UUID.randomUUID().toString()
//            }
//            AssessExecuteExt.execute(a, path)
//            if (a.partialRefinements.isNotEmpty() && a.partialRefinements[0].labelingFunction == null) {
//                refine(a.partialRefinements[0], false)
//            }
//        }
//        try {
//                listOf(1, 3).forEach { k ->
//                    (1..3).forEach { r ->
//                        listOf("ssbora", "ssbora5", "ssbora10", "ssbora15").forEach { c -> // , "ssbora15", "ssbora10", "ssbora5", "ssbora"
//                            // refine(AssessExecuteExt.parse("with $c by nation, year for nation = 'GERMANY' and year = '1992' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by s_nation, nation for nation = 'GERMANY' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by s_nation, nation, category for nation = 'GERMANY' and category = 'MFGR#31' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by s_nation, nation, category, year for nation = 'GERMANY' and category = 'MFGR#31' and year = '1992' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by nation, category for nation = 'GERMANY' and category = 'MFGR#31' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by nation, year for nation = 'GERMANY' and year = '1992' assess quantity", k))
//
//                            // refine(AssessExecuteExt.parse("with $c by nation for nation = 'GERMANY' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c by category for category = 'MFGR#31' assess quantity", k))
//                            // refine(AssessExecuteExt.parse("with $c assess quantity by nation", k))
//                            // refine(AssessExecuteExt.parse("with $c assess quantity by nation, category", k))
//                            // refine(AssessExecuteExt.parse("with $c by nation, category, year assess quantity", k))
//
//                            refine(AssessExecuteExt.parse("with $c by nation, category, year for nation = 'GERMANY' and category = 'MFGR#12' and year = '1992' assess quantity", k))
//                            refine(AssessExecuteExt.parse("with $c by year, category for year = '1992' and category = 'MFGR#12' assess quantity", k))
//                            refine(AssessExecuteExt.parse("with $c by year for year = '1992' assess quantity", k))
//
//                            refine(AssessExecuteExt.parse("with $c by year assess quantity", k))
//                            refine(AssessExecuteExt.parse("with $c by year, category assess quantity", k))
//                            refine(AssessExecuteExt.parse("with $c by nation, category, year assess quantity", k))
//                        }
//                    }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            fail(e.message)
//        }
//    }

    @Test
    fun testAssess1() {
        try {
            val i: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against region scaled population using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(i, path)
            val h: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation for nation = 'GERMANY' against nation = 'FRANCE' scaled population using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(h, path)
            val d: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, brand for nation = 'GERMANY' and category = 'MFGR#31'")
            AssessExecuteExt.execute(d, path)
            val c: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by region, category for category = 'MFGR#31'")
            AssessExecuteExt.execute(c, path)
            val b: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, category for nation = 'GERMANY' and category = 'MFGR#31'")
            AssessExecuteExt.execute(b, path)
            val a: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation, category for nation = 'GERMANY'")
            AssessExecuteExt.execute(a, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental1() {
        try {
            val a: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation")
            AssessExecuteExt.execute(a, path)
            val b: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against region scaled population")
            AssessExecuteExt.execute(b, path)
            val c: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against REGION scaled POPULATION using difference(QUANTITY, benchmark.QUANTITY)")
            AssessExecuteExt.execute(c, path)
            val d: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation against region scaled population using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(d, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental2() {
        try {
            // val a: AssessExt = AssessExecuteExt.parse("with ssbora by nation for nation = 'FRANCE' assess quantity")
            // AssessExecuteExt.execute(a, path)
            val b: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation for nation = 'FRANCE' against nation = 'GERMANY' scaled population")
            AssessExecuteExt.execute(b, path)
            val c: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation for nation = 'FRANCE' against nation = 'GERMANY' scaled population using difference(quantity, benchmark.quantity)")
            AssessExecuteExt.execute(c, path)
            val d: AssessExt = AssessExecuteExt.parse("with ssbora assess quantity by nation for nation = 'FRANCE' against nation = 'GERMANY' scaled population using difference(quantity, benchmark.quantity) labels quartile")
            AssessExecuteExt.execute(d, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental3() {
        try {
            var b: AssessExt = AssessExecuteExt.parse("with covid19 assess deaths by country against continent")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France'")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France' using ratio(deaths, benchmark.deaths)")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France' using ratio(deaths, benchmark.deaths) labels quartile")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina'")
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental4() {
        try {
            var b: AssessExt = AssessExecuteExt.parse("with covid19 assess deaths by country against continent")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France' scaled population")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France' scaled population using ratio(deaths, benchmark.deaths)")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with COVID19 assess deaths by country for country='Bosnia And Herzegovina' against country='France' scaled population using ratio(deaths, benchmark.deaths) labels quartile")
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental5() {
        try {
            var b: AssessExt = AssessExecuteExt.parse("with covid19 assess deaths by country against continent")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with covid19 assess deaths by country against continent using ratio(deaths, benchmark.deaths)")
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with covid19 assess deaths by country against continent using ratio(deaths, benchmark.deaths) labels quartile")
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental6() {
        try {
            try {
                var b: AssessExt
                // TODO This is BROKEN, cannot compare against grand parent but only against parent
                // b = AssessExecuteExt.parse("with COVID19 by country assess cases against allcountry")
                // AssessExecuteExt.execute(b, path)
                b = AssessExecuteExt.parse("with COVID19 assess deaths by country, year for country='France' against country='United Kingdom' using difference(DEATHS, benchmark.DEATHS)")
                AssessExecuteExt.execute(b, path)
                b = AssessExecuteExt.parse("with COVID19 assess cases by country, month for country='Italy'")
                AssessExecuteExt.execute(b, path)
                b = AssessExecuteExt.parse("with covid19 assess deaths by country against continent scaled population")
                AssessExecuteExt.execute(b, path)
                b = AssessExecuteExt.parse("with covid19 assess deaths by country against continent scaled population using ratio(deaths, benchmark.deaths)")
                AssessExecuteExt.execute(b, path)
                b = AssessExecuteExt.parse("with covid19 assess deaths by country against continent scaled population using ratio(deaths, benchmark.deaths) labels quartile")
                AssessExecuteExt.execute(b, path)
            } catch (e: Exception) {
                e.printStackTrace()
                fail(e.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental7() {
        try {
            try {
                var b: AssessExt
                listOf("deaths", "cases").forEach {
                    m ->
                    b = AssessExecuteExt.parse("with covid19 assess $m by week for continent = 'Europe'")
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country for country='France'")
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by week for continent = 'Europe'")
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, year for country='France'")
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country for country='France'")
                    AssessExecuteExt.execute(b, path)
                }
                // b = AssessExecuteExt.parse("with covid19 by week assess deaths")
                // AssessExecuteExt.execute(b, path)
            } catch (e: Exception) {
                e.printStackTrace()
                fail(e.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental8() {
        try {
            var b: AssessExt
            listOf("deaths", "cases").forEach { m ->
                listOf("", "week", "month", "year").forEach { a1 ->
                    listOf("", "country", "continent").forEach { a2 ->
                        if (a1.isNotEmpty() || a2.isNotEmpty()) {
                            val attrs = if (a1.isNotEmpty() && a2.isNotEmpty()) {
                                "$a1, $a2"
                            } else {
                                if (a1.isNotEmpty()) {
                                    a1
                                } else {
                                    a2
                                }
                            }
                            val i = "with covid19 assess $m by $attrs for continent = 'Europe' and year='2020'"
                            println(i)
                            b = AssessExecuteExt.parse(i)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental9() {
        try {
            listOf(1, 3).forEach { k ->
                listOf("deaths", "cases").forEach { m ->
                    var b: AssessExt
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' and year='2020' against country='Andorra' scaled population using reldifference($m, benchmark.$m)", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' against country='Andorra' scaled population using reldifference($m, benchmark.$m)", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' against country='Malta' scaled population using reldifference($m, benchmark.$m)", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' and year='2020' against country = 'Malta' scaled population", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' and year='2020' against continent scaled population", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with COVID19 assess $m by country, month for country='Italy' and year='2020'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by country for country='France'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by month, country for country='Italy'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by week, country for week='2020-01-01'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by month, country for month='2020-01'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by year, country for continent = 'Europe' and year='2020'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by year, continent for continent = 'Europe' and year='2020'", k)
                    AssessExecuteExt.execute(b, path)
                    b = AssessExecuteExt.parse("with covid19 assess $m by year for continent = 'Europe' and year='2020'", k)
                    AssessExecuteExt.execute(b, path)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental10() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("with covid19 assess deaths by year, country for continent = 'Europ' and year='2020'", 1)
            AssessExecuteExt.execute(b, path)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testIncremental11() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("with covid19 assess deaths by year, country for continent = 'Europe' and month='2020-01'", 1)
            AssessExecuteExt.execute(b, path)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testIncremental12() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("with covid19 assess deaths by week, country for week='2020-01-10'", 1)
            AssessExecuteExt.execute(b, path)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testIncremental14() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE_EXT assess consototale by epci against epci='248200099 CA Grand Montauban'", 1)
            AssessExecuteExt.execute(b, path)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testIncremental13() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("with consommation_electrique_ext assess consototale by annee, categorieconsommation for categorieconsommation='Entreprise' against ALLANNEE", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with consommation_electrique assess consototale by typeEPCI ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with consommation_electrique assess consototale by typeIRIS ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with consommation_electrique assess consototale by typeIRIS, annee ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by commune for commune = '35238 Rennes' ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by commune for commune = '35238 Rennes' against epci scaled popcomm", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by commune, categorieconsommation, annee for commune='35238 Rennes' and categorieconsommation = 'Entreprise' ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by iris for commune = '35238 Rennes' ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE_EXT assess consototale by epci for epci='244400404 Nantes Metropole' against epci='248200099 CA Grand Montauban'", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by secteurnaf2 ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by annee ", 1)
            AssessExecuteExt.execute(b, path)
            // The last three tests will fail if looking for duplicates in the cube. EPCIs are not unique in the database!!
            b = AssessExecuteExt.parse("with consommation_electrique assess consototale by EPCI, annee for categorieconsommation = 'Entreprise' ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by epci ", 1)
            AssessExecuteExt.execute(b, path)
            b = AssessExecuteExt.parse("with CONSOMMATION_ELECTRIQUE assess consototale by epci against typeepci", 1)
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental15() {
        try {
            var b: AssessExt
            b = AssessExecuteExt.parse("WITH CONSOMMATION_ELECTRIQUE assess consototale by commune ", 1)
            var vertexes = DependencyGraph.getDependencies(b.cube).vertexSet().filter { !it.startsWith("pop") && !it.startsWith("all") }.toList()
            vertexes += ""
            vertexes.withIndex().forEach { i1 ->
                vertexes.withIndex().forEach { i2 ->
                    val a1 = i1.value
                    val a2 = i2.value
                    // TODO: i2.index < 4 is added to keep the tests short, comment it to check all the combinations of 2 attributes
                    if ((i2.index < 4 && i1.index < i2.index) && (a1.isEmpty() || a2.isEmpty() || !DependencyGraph.lca(b.cube, a1, a2).isPresent)) {
                        val attrList = listOf(a1, a2).filter { it.isNotEmpty() }
                        val attrs = attrList.stream().reduce { a, b -> "$a,$b" }.get()
                        val i = "WITH CONSOMMATION_ELECTRIQUE assess consototale by $attrs "
                        println(i)
                        b = AssessExecuteExt.parse(i)
                        AssessExecuteExt.execute(b, path)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fail(e.message)
        }
    }

    @Test
    fun testIncremental16() {
        try {
            val b = AssessExecuteExt.parse("WITH CONSOMMATION_ELECTRIQUE_EXT consototale by epci FOR epci='200065597 CU Caen la Mer' ", 1)
            AssessExecuteExt.execute(b, path)
            fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun testIncremental17() {
        try {
            val b = AssessExecuteExt.parse("WITH CONSOMMATION_ELECTRIQUE assess consototale by commune, epci ", 1)
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            fail()
        }
    }

    @Test
    fun testIncremental18() {
        try {
            val b: AssessExt = AssessExecuteExt.parse("WITH CONSOMMATION_ELECTRIQUE assess consototale by region, typeepci ", 1)
            AssessExecuteExt.execute(b, path)
        } catch (e: Exception) {
            fail()
        }
    }
}
