package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.application.TrilogyApplicationOptions
import io.pivotal.trilogy.testproject.UrlTestCaseResourceLocator
import io.pivotal.trilogy.testproject.UrlTestProjectResourceLocator
import org.jetbrains.spek.api.Spek
import java.io.File
import kotlin.test.expect

class TrilogyApplicationOptionsTests : Spek({
    it("should provide a project resource locator") {
        val projectPath = "src/test/resources/projects/blank/"
        val resourceLocator = TrilogyApplicationOptions(testProjectPath = projectPath, shouldDisplayHelp = false).resourceLocator
        expect(true) { resourceLocator is UrlTestProjectResourceLocator }
        expect(File(projectPath).toURI().toURL()) { (resourceLocator as UrlTestProjectResourceLocator).projectUrl }
    }

    it("should provide a test case resource locator") {
        expect(true) { TrilogyApplicationOptions(testCaseFilePath = "src/test/resources/testcases/generic.stt", shouldDisplayHelp = false).resourceLocator is UrlTestCaseResourceLocator }
    }
})