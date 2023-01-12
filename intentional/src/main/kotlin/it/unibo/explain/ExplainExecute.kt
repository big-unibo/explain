package it.unibo.explain

import it.unibo.Intention
import it.unibo.antlr.gen.ExplainLexer
import it.unibo.antlr.gen.ExplainParser
import it.unibo.antlr.gen.ThrowingErrorListener
import it.unibo.conversational.database.QueryGenerator
import krangl.DataFrame
import krangl.readCSV
import krangl.rename
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.json.JSONObject
import org.slf4j.LoggerFactory

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
    fun execute(d: Explain, path: String, pythonPath: String = "src/main/python/"): Triple<JSONObject, DataFrame, DataFrame> {
        val allmeasures = QueryGenerator.getMeasures(d.cube)
        val othermeasures = allmeasures.minus(d.measures.first())
        d.addMeasures(*othermeasures.toTypedArray())
        val timeQuery = d.writeMultidimensionalCube(path)
        L.warn("Computing models...")
        val timeModel = d.computePython(pythonPath, path, "explain.py")
        L.warn("Models computed")
        d.measures.removeIf { othermeasures.contains(it) }
        val s = "$path${d.filename}_${d.sessionStep}"
        val cube: DataFrame = DataFrame.readCSV("$s.csv") // .sortedBy(*coordinate.toTypedArray())
        val prop: DataFrame = DataFrame.readCSV("${s}_property.csv") // .sortedBy(*coordinate.toTypedArray())
        L.warn("Read $s")
        d.statistics["time_query"] = timeQuery
        d.statistics["time_model"] = timeModel
        d.statistics["cardinality"] = cube.nrow
        return Triple(JSONObject(), cube, prop)
    }
}