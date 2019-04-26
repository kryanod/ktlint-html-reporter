package me.cassiano.ktlint.reporter.html

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.core.Reporter
import java.io.PrintStream
import java.util.concurrent.ConcurrentHashMap

class HtmlReporter(private val out: PrintStream) : Reporter {

    private val acc = ConcurrentHashMap<String, MutableList<LintError>>()
    private var issueCount = 0
    private var correctedCount = 0

    override fun onLintError(file: String, err: LintError, corrected: Boolean) {
        if (!corrected) {
            issueCount += 1
            acc.getOrPut(file) { mutableListOf() }.add(err)
        } else {
            correctedCount += 1
        }
    }

    override fun afterAll() {
        html {
            head {
                cssLink("https://fonts.googleapis.com/css?family=Source+Code+Pro")
                text("<style>\n")
                text("body {\n")
                text("    font-family: 'Source Code Pro', monospace;\n")
                text("}\n")
                text("h3 {\n")
                text("    font-size: 12pt;\n")
                text("}")
                text("</style>\n")
            }
            body {
                if (!acc.isEmpty()) {

                    h1 { text("Overview") }

                    paragraph {
                        text("Issues found: $issueCount")
                    }

                    paragraph {
                        text("Issues corrected: $correctedCount")
                    }

                    acc.forEach { file: String, errors: MutableList<LintError> ->
                        h3 { text(file) }
                        ul {
                            errors.forEach { (line, col, ruleId, detail) ->
                                item("($line, $col): $detail  ($ruleId)")
                            }
                        }
                    }
                } else {
                    paragraph {
                        text("Congratulations, no issues found!")
                    }
                }
            }
        }
    }

    private fun html(body: () -> Unit) {
        out.println("<html>")
        body()
        out.println("</html>")
    }

    private fun head(body: () -> Unit) {
        out.println("<head>")
        body()
        out.println("</head>")
    }

    private fun body(body: () -> Unit) {
        out.println("<body>")
        body()
        out.println("</body>")
    }

    private fun h1(body: () -> Unit) {
        out.print("<h1>")
        body()
        out.println("</h1>")
    }

    private fun h3(body: () -> Unit) {
        out.print("<h3>")
        body()
        out.println("</h3>")
    }

    private fun text(value: String) {
        out.print(value)
    }

    private fun ul(body: () -> Unit) {
        out.println("<ul>")
        body()
        out.println("</ul>")
    }

    private fun item(value: String) {
        out.print("<li>")
        text(value)
        out.println("</li>")
    }

    private fun cssLink(link: String) {
        out.print("<link href=\"")
        out.print(link)
        out.println("\" rel=\"stylesheet\" />")
    }

    private fun paragraph(body: () -> Unit) {
        out.print("<p>")
        body()
        out.println("</p>")
    }
}
