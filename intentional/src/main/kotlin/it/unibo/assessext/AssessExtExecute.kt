package it.unibo.assessext

import com.google.common.collect.Lists
import it.unibo.Intention
import it.unibo.antlr.gen.AssessLexer
import it.unibo.antlr.gen.AssessParser
import it.unibo.antlr.gen.ThrowingErrorListener
import it.unibo.assess.Assess
import it.unibo.assess.Assess.BenchmarkType
import it.unibo.assess.CustomAssessVisitor
import it.unibo.conversational.database.QueryGenerator
import it.unibo.conversational.datatypes.DependencyGraph
import it.unibo.describe.DescribeExecute.getPivot
import krangl.DataFrame
import krangl.mean
import krangl.readCSV
import krangl.summarize
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import org.json.JSONObject
import java.io.File
import kotlin.math.roundToInt

/**
 * Assess operator in action
 */
object AssessExecuteExt {

    /**
     * Parse intention from text.
     *
     * @param input current operator
     * @return new operator
     */
    fun parse(input: String?, k: Int = 3): AssessExt {
        val lexer = AssessLexer(ANTLRInputStream(input)) // new ANTLRInputStream(System.in);
        lexer.addErrorListener(ThrowingErrorListener())
        val tokens = CommonTokenStream(lexer) // create a buffer of tokens pulled from the lexer
        val parser = AssessParser(tokens) // create a parser that feeds off the tokens buffer
        parser.addErrorListener(ThrowingErrorListener())
        val tree: ParseTree = parser.assess() // begin parsing at init rule
        val v = CustomAssessVisitor(AssessExt())
        v.visit(tree)
        return AssessExt.from(v.assess, k)
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun execute(d: Intention, path: String?, pythonPath: String? = "src/main/python/"): JSONObject {
        println(path)
        d.computePython(pythonPath, path, "assess_ext.py")
        val ret = JSONObject()
        // (0 until d.k).map { // TODO: undo these comments to enable the partial refinement
            // val newPath = path + "${d.filename}_$it.json"
        val newPath = path + "${d.filename}_${d.sessionStep}.json"
        if (File(newPath).exists()) {
            val json = File(newPath).readText(Charsets.UTF_8)
            val jsonObj = JSONObject(json)
            // val newd: AssessExt = AssessExt.from(d as Assess, d.k) // TODO: undo these comments to enable the partial refinement
            val newd: AssessExt = AssessExt.from(d as Assess, 1)

            // verify if we are dealing with SIBLING or PARENT
            val benchmark: String = Lists.newArrayList(jsonObj.get("against"))[0].toString()
            // if the benchmark contains an attribute rolling up the current group by... then it is a parent benchmark
            if (DependencyGraph.getDependencies(d.cube).containsVertex(benchmark.replace("'", "").lowercase())) {
                newd.benchmark = benchmark.replace("'", "")
                newd.benchmarkType = BenchmarkType.PARENT
            } else {
                // else we are dealing with a sibling
                val possibleAttributes = QueryGenerator.getLevelsFromMember(newd.cube, benchmark.replace("'", "")).map { it.lowercase() }
                val forclause = d.clauses.filter { possibleAttributes.contains(it.left.lowercase()) }[0]
                newd.benchmark = Triple.of(forclause.left, forclause.middle, listOf(benchmark))
                newd.benchmarkType = BenchmarkType.SIBLING
            }

            if (!jsonObj.getJSONObject("using").isEmpty) {
                val func = jsonObj.getJSONObject("using")
                val functionName: String = func.getString("fun")
                val params: Array<String> = func.getJSONArray("params").map { it.toString() }.toTypedArray()
                newd.setFunction(newd.prepareFunction(functionName, params))
            }

            if (jsonObj.getString("label").isNotEmpty()) {
                val label: String = jsonObj.getString("label")
                newd.labelingFunction = label
            }

            if (jsonObj.getString("scaled").isNotEmpty()) {
                newd.scaled = jsonObj.getString("scaled")
            }

            newd.defaultFunctions = jsonObj.getString("def_using")
            newd.defaultLabelingFunction = jsonObj.getString("def_label")
            newd.filename = "${d.filename}_${d.sessionStep}.json"
            // d.partialRefinements += newd // TODO: undo these comments to enable the partial refinement
            jsonObj.put("intention", newd.toString())
            jsonObj.put("type", "assess")
            val cube = DataFrame.readCSV(newPath.replace(".json", "_enhanced.csv"))
            val p = getPivot(d, cube)
            jsonObj.put("pivot", p)

            var properties = cube.groupBy("label").summarize("properties") { Pair.of("mean", (it["comparison"].mean() as Double * 100.0).roundToInt() / 100.0) }
            properties = properties.addColumn("interest") { "-" }
            properties.sortedBy("label").rows.forEach {
                val rowJson = JSONObject()
                rowJson.put("label", "label=" + it["label"].toString())
                rowJson.put("properties", it["properties"])
                jsonObj.append("components", rowJson)
            }

            ret.put(newd.id, jsonObj)
        }
        // }
        return ret
    }
}