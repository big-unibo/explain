package it.unibo.predict

import it.unibo.antlr.gen.PredictBaseVisitor
import it.unibo.antlr.gen.PredictParser
import it.unibo.antlr.gen.PredictParser.PredictContext
import org.apache.commons.lang3.tuple.Triple
import org.json.JSONObject
import java.util.stream.Collectors

/** How to interpret the Predict syntax. */
class CustomPredictVisitor(val explain: Predict) : PredictBaseVisitor<JSONObject?>() {

    override fun visitPredict(ctx: PredictContext): JSONObject? {
        explain.setCube(ctx.cube.name)
        explain.addAttribute(true, *ctx.gc.stream().map { it.name }.toArray())
        if (ctx.sc != null) {
            visit(ctx.sc)
        }
        explain.addMeasures(ctx.mc.text.lowercase())
        explain.against += ctx.against.map { it.text.lowercase() }
        explain.using += ctx.using.map { it.name }
        return null
    }

    override fun visitCondition(ctx: PredictParser.ConditionContext): JSONObject? {
        explain.addClause(
            Triple.of(ctx.attr.text, if (ctx.op == null) ctx.`in`.text else ctx.op.text, ctx.`val`.stream().map { obj: PredictParser.ValueContext -> obj.text }.collect(Collectors.toList()))
        )
        return null
    }
}