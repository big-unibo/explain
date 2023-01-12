package it.unibo.explain

import com.google.common.collect.Lists
import it.unibo.Intention
import it.unibo.antlr.gen.ExplainLexer
import it.unibo.antlr.gen.ExplainParser
import it.unibo.antlr.gen.ThrowingErrorListener
import krangl.*
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.math.roundToInt

/**
 * Explain intention in action.
 */
object ExplainExecute {
    private val L = LoggerFactory.getLogger(ExplainExecute::class.java)

    fun parse(input: String): Explain {
        val lexer = ExplainLexer(ANTLRInputStream(input)) // new ANTLRInputStream(System.in);
        val tokens = CommonTokenStream(lexer) // create a buffer of tokens pulled from the lexer
        val parser = ExplainParser(tokens) // create a parser that feeds off the tokens buffer
        parser.addErrorListener(ThrowingErrorListener())
        val tree: ParseTree = parser.explain() // begin parsing at init rule
        val v = CustomExplainVisitor(Explain())
        v.visit(tree)
        return v.explain
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun execute(d: Intention, path: String, pythonPath: String = "src/main/python/", makePivot: Boolean = true): Pair<JSONObject, DataFrame> {
        val timeQuery = d.writeMultidimensionalCube(path)
        L.warn("Computing models...")
        val timeModel = d.computePython(pythonPath, path, "Explain.py")
        L.warn("Models computed")
        val coordinate: Set<String> = d.attributes
        var cube: DataFrame = DataFrame.readCSV("$path${d.filename}_${d.sessionStep}_ext.csv").sortedBy(*coordinate.toTypedArray())
        L.warn("Read $path${d.filename}_${d.sessionStep}_ext.csv")
        d.statistics["time_query"] = timeQuery
        d.statistics["time_model"] = timeModel
        d.statistics["cardinality"] = cube.nrow

        val json = JSONObject()
        if (makePivot) {
            var properties = DataFrame.readCSV(path + d.filename + "_" + d.sessionStep + "_properties.csv")
            // TODO only the first property is kept
            properties = properties.groupBy("model", "component").summarize("properties") { Pair(it["property"].map<Any> { it.toString() }.first(), it["value"].map<Any> { it.toString() }.first()) }
            properties = properties.addColumn("interest") {
                it["model"]
                        .map<Any> { it }
                        .zip(it["component"].map<Any> { it.toString().lowercase() })
                        .map { p ->
//                            val pair = modelInterest[p.first!!]!![parseVal(p.second!!.toString())]!!
//                            pair.left / pair.right
                            0
                        }
            }
            properties = properties.sortedByDescending("interest")
            val model: String = properties["model"].get(0).toString()
            properties.filterByRow { it["model"] == model }.rows.forEach {
                val rowJson = JSONObject()
                rowJson.put("component", it["model"] as String + "=" + it["component"].toString().lowercase())
                rowJson.put("interest", (it["interest"] as Double * 1000).roundToInt() / 1000.0)
                rowJson.put("properties", it["properties"])
                json.append("components", rowJson)
            }
            properties.writeCSV(File(path + d.filename + "_" + d.sessionStep + "_model.csv"))

            /* ***********************************************************************
             * WRITE TO FILE
             * **********************************************************************/
            var startTime = System.currentTimeMillis()
            cube = cube.addColumn("label") { it[model] }
            cube.rows.forEach {
                val rowJson = JSONObject()
                cube.names.forEach { name -> rowJson.put(name, it[name]) }
                json.append("raw", rowJson)
                d.measures.forEach { mea ->
                    val redJson = JSONObject()
                    cube.names.forEach { name ->
                        redJson.put(name, it[name])
                        if (name == mea) {
                            redJson.put("measure", name)
                            redJson.put("value", it[name])
                        }
                        json.append("red", redJson)
                    }
                }
            }

            val p = getPivot(d, cube)
            json.put("pivot", p)
            json.put("measures", p.getJSONObject("headers").getJSONArray("measures"))
            if (p.getJSONObject("headers").has("dimensions")) {
                json.put("dimensions", p.getJSONObject("headers").getJSONArray("dimensions"))
            }
            json.put("intention", d.toString())
            json.put("type", "Explain")
            d.statistics["pivot_time"] = System.currentTimeMillis() - startTime
        } else {
            d.statistics["pivot_time"] = 1 // set default time to 1 ms (otherwise charts with logarithmic scale won't work)
        }
        cube.writeCSV(File(path + d.filename + "_" + d.sessionStep + "_enhanced.csv"))
        val ret = JSONObject()
        ret.put(d.id, json)
        return Pair(ret, cube)
    }

    fun getPivot(d: Intention, cube: DataFrame): JSONObject {
        val header = cube.names
        val currSchema = cube.names.withIndex().filter { d.attributes.contains(it.value) }.map { it.index }.toIntArray()
        val data: MutableList<List<String>> = Lists.newArrayList()
        for (r in cube.rows) {
            val row: MutableList<String> = Lists.newArrayList()
            r.values.forEach { v: Any? -> row.add(v.toString()) }
            data.add(row)
        }
        return Intention.getPivot(d, currSchema, data, header)
    }

    /**
     * Get maximum value among many columns
     */
    fun myMax(vararg cols: DataCol): Array<Double?> {
        if (cols.isEmpty()) {
            throw IllegalArgumentException("Empty columns")
        }
        if (cols.size == 1) {
            return cols[0].toDoubles()
        }
        return cols
                .map { it.toDoubles().toList() }
                .reduce { a1, a2 -> a1.zip(a2).map { (it.first!!).coerceAtLeast(it.second!!) } }
                .toTypedArray()
    }

    fun parseVal(s: String): Any {
        try {
            if (s.lowercase().equals("true") || s.lowercase().equals("false"))
                return java.lang.Boolean.parseBoolean(s)
            else throw IllegalArgumentException()
        } catch (e: Exception) {
            try {
                return java.lang.Integer.parseInt(s)
            } catch (e: Exception) {
                try {
                    return java.lang.Double.parseDouble(s)
                } catch (e: Exception) {
                    return s
                }
            }
        }
    }
}