package it.unibo.explain

import it.unibo.antlr.gen.ExplainBaseVisitor
import it.unibo.antlr.gen.ExplainParser
import it.unibo.antlr.gen.ExplainParser.ExplainContext
import org.apache.commons.lang3.tuple.Triple
import org.json.JSONObject
import java.util.stream.Collectors

/** How to interpret the Explain syntax. */
class CustomExplainVisitor(val explain: Explain) : ExplainBaseVisitor<JSONObject?>() {

    override fun visitExplain(ctx: ExplainContext): JSONObject? {
        explain.setCube(ctx.cube.name)
        explain.addAttribute(true, *ctx.gc.stream().map { it.name }.toArray())
        if (ctx.sc != null) {
            visit(ctx.sc)
        }
        explain.addMeasures(ctx.mc.name)
        return null
    }

    override fun visitCondition(ctx: ExplainParser.ConditionContext): JSONObject? {
        explain.addClause(
            Triple.of(ctx.attr.text, if (ctx.op == null) ctx.`in`.text else ctx.op.text, ctx.`val`.stream().map { obj: ExplainParser.ValueContext -> obj.text }.collect(Collectors.toList()))
        )
        return null
    }
}