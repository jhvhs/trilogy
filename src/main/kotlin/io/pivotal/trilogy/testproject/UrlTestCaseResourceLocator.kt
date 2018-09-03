package io.pivotal.trilogy.testproject

import io.pivotal.trilogy.i18n.MessageCreator
import io.pivotal.trilogy.testcase.InvalidTestCaseName
import io.pivotal.trilogy.testcase.TestCaseNotFound
import java.io.File
import java.net.URL

class UrlTestCaseResourceLocator(url: URL) : TestProjectResourceLocator {
    init {
        if (url.isInvalid) throw InvalidTestCaseName(MessageCreator.getI18nMessage("testCaseRunner.errors.invalidTestCaseName", listOf(url.path)))

        if (! File(url.path).isFile) throw TestCaseNotFound(MessageCreator.getI18nMessage("testCaseRunner.errors.testCaseNotFound", listOf(url.path)))
    }
    override val testCases = listOf(TestCaseResource(url.path ,url.textContent)).sortedBy { it.path }

    private val URL.isInvalid: Boolean get() = !isValid
    private val URL.isValid: Boolean get() = file.toLowerCase().endsWith(".stt")
    private val URL.textContent: String get() = File(toURI()).readText()
}