package it.unibo.describe

import com.google.common.base.Optional
import com.google.common.collect.Sets
import it.unibo.Intention
import org.apache.commons.lang3.tuple.Triple
import java.io.File

class Describe : Intention {
    private var models: Set<String> = Sets.newLinkedHashSet()
    var k = Optional.absent<Int>()

    constructor(d: Intention?, accumulateAttributes: Boolean) : super(d, accumulateAttributes) {}
    constructor(accumulateAttributes: Boolean) : super(null, accumulateAttributes) {}

    fun getModels(): Set<String> {
        return if (models.isEmpty()) Sets.newHashSet("top-k", "bottom-k", "clustering", "outliers", "skyline") else models
    }

    fun setModels(models: List<String>) {
        this.models = models.toSet()
    }

    override fun toPythonCommand(commandPath: String, path: String): String {
        val sessionStep = getSessionStep()
        val filename = getFilename()
        val fullCommand = (commandPath.replace("/", File.separator) //
                + " --path " + (if (path.contains(" ")) "\"" else "") + path.replace("\\", "/") + (if (path.contains(" ")) "\"" else "") //
                + " --file " + filename //
                + " --session_step " + sessionStep //
                + (if (k.isPresent) " --k " + k.get() else "") //
                + " --computeproperty " + (if (computeProperty) "True" else "False") //
                + " --models " + getModels().stream().reduce("") { a: String, b: String -> "$a $b" } //
                + " --cube " + json.toString().replace(" ", "__"))
        L.warn(fullCommand)
        return fullCommand
    }

    companion object {
        var id = 0
        var computeProperty = true
    }

    fun clauseToString(o: Any): String {
        return if (o is Triple<*, *, *>) {
            val c = o as Triple<String, String, List<String>>
            return c.left + c.middle + c.right[0]
        } else {
            o.toString()
        }
    }

    override fun toString(): String {
        return "with ${cubeSyn} " +
                "describe ${measures.reduce { a, b -> "$a, $b" }} " +
                "by ${attributes.reduce { a, b -> "$a, $b" }} " +
                if (clauses.isEmpty()) { "" } else { "for ${clauses.toList().map { clauseToString(it) }.reduce { a, b -> "$a and $b"} } " } +
                "using ${getModels()}"
    }
}