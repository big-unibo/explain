package it.unibo.explain

import it.unibo.Intention
import it.unibo.describe.Describe.Companion.clauseToString
import java.io.File
import java.util.*

class Explain : Intention {
    constructor(d: Intention?) : super(d, false) {}
    constructor() : super(null, false) {}
    val against: MutableSet<String> = mutableSetOf()
    val using: MutableSet<String> = mutableSetOf()

    fun concat(c: Collection<String>, sep: String =", "): String {
        if (c.isEmpty()) {
            return ""
        }
        return c.toList().sorted().reduce { a, b -> "$a$sep$b"}
    }

    override fun toPythonCommand(commandPath: String, path: String): String {
        val sessionStep = getSessionStep()
        val filename = getFilename()
        val execution_id = UUID.randomUUID().toString()
        statistics["execution_id"] = execution_id
        return (commandPath.replace("/", File.separator) //
                + " --path " + (if (path.contains(" ")) "\"" else "") + path.replace("\\", "/") + (if (path.contains(" ")) "\"" else "") //
                + " --file " + filename //
                + " --session_step " + sessionStep //
                + " --measure " + measures.minus(against).first() //
                + " --execution_id " + execution_id
                + " --cube " + json.toString().replace(" ", "__")
                + " --against " + concat(against, sep = ",")
                + " --using " + concat(using, sep = ","))
    }

    override fun toString(): String {
        return "with $cubeSyn" +
                " explain ${concat(measures)}" +
                " by ${concat(attributes)}" +
                if (clauses.isEmpty()) { "" } else { " for ${concat(clauses.map { clauseToString(it) }, sep = " and ")}" } +
                " against " + if (against.isEmpty()) { measures } else { concat(against) } +
                " using " + if (using.isEmpty()) { concat(listOf("CrossCorrelation", "Multireg", "Polyfit")) } else { concat(using) }
    }
}