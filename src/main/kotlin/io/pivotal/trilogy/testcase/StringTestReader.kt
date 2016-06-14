package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.parsing.MarkdownTable

class StringTestReader(val testBody: String) : TestReader {
    class InvalidTestFormat(message: String?) : RuntimeException(message) {}
    class MissingDataSection(message: String?) : RuntimeException(message) {}
    class MissingDescription(message: String?) : RuntimeException(message) {}

    private val dataSectionHeader = "### DATA\\n"
    private val headerRow = "\\s*\\|.*?\\n"
    private val headerSeparationRow = "\\s*\\|([:-]+\\|)+\\n"
    private val valueRows = "(\\s*\\|.+?\\n)+"

    private val testHeaderRegex = Regex("\\A\\s*## TEST\\s*")

    override fun getTest(): TrilogyTest {
        return parse()
    }

    init {
        validate()
    }

    private fun parse(): TrilogyTest {
        return TrilogyTest(parseDescription(), parseArgumentTable())
    }

    private fun parseArgumentTable(): TestArgumentTable {
        val dataSection = testBody.replace(Regex("\\A.*?$dataSectionHeader\\s*", RegexOption.DOT_MATCHES_ALL), "").trim()
        val table = MarkdownTable(dataSection)
        return TestArgumentTable(table.getHeaders(), table.getValues())
    }

    private fun parseDescription(): String {
        val description = testBody.replace(testHeaderRegex, "").replace(Regex("\\s*### DATA.*", RegexOption.DOT_MATCHES_ALL), "").trim()
        if (description.isEmpty()) throw MissingDescription("Every test should have a description")
        return description
    }

    private fun validate() {
        if (!testBody.hasValidFormat()) throw InvalidTestFormat("Unable to recognise the test")
        if (!testBody.hasDataSection()) throw MissingDataSection("The test is missing a data section")
    }


    private fun String.hasValidFormat(): Boolean = this.contains(testHeaderRegex)

    private fun String.hasDataSection(): Boolean {
        return this.contains(Regex(dataSectionHeader + headerRow + headerSeparationRow + valueRows))
    }
}