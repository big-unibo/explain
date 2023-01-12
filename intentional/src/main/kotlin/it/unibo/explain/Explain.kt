package it.unibo.explain

import it.unibo.Intention
import it.unibo.conversational.database.QueryGenerator
import it.unibo.describe.Describe.Companion.clauseToString
import java.io.File

class Explain : Intention {
    constructor(d: Intention?) : super(d, false) {}
    constructor() : super(null, false) {}

    override fun toPythonCommand(commandPath: String, path: String): String {
        val sessionStep = getSessionStep()
        val filename = getFilename()
        val fullCommand = (commandPath.replace("/", File.separator) //
                + " --path " + (if (path.contains(" ")) "\"" else "") + path.replace("\\", "/") + (if (path.contains(" ")) "\"" else "") //
                + " --file " + filename //
                + " --session_step " + sessionStep //
                + " --measure " + measures.first() //
                + " --cube " + json.toString().replace(" ", "__"))
        return fullCommand
    }

    override fun toString(): String {
        return "with $cubeSyn " +
                "explain ${measures.reduce { a, b -> "$a, $b" }} " +
                "by ${attributes.reduce { a, b -> "$a, $b" }} " +
                if (clauses.isEmpty()) { "" } else { "for ${clauses.toList().map { clauseToString(it) }.reduce { a, b -> "$a and $b"} } " }
    }
}