package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.ResourceHelper
import io.pivotal.trilogy.shouldNotThrow
import io.pivotal.trilogy.shouldThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.expect

class GenericStringTestCaseParserTest : Spek({
    context("minimal") {
        val validTestCase = ResourceHelper.getTestCaseByName("genericMinimal")

        it("parses") {
            GenericStringTestCaseParser(validTestCase).getTestCase()
        }

        it("reads the test case description") {
            expect("Minimal generic test") { GenericStringTestCaseParser(validTestCase).getTestCase().description }
        }

        it("has no hooks") {
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.beforeAll.size }
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.afterAll.size }
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.beforeEachTest.size }
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.afterEachTest.size }
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.beforeEachRow.size }
            expect(0) { GenericStringTestCaseParser(validTestCase).getTestCase().hooks.afterEachRow.size }
        }

        it("parses the test") {
            expect(1) { GenericStringTestCaseParser(validTestCase).getTestCase().tests.size }
            expect("SELECT * FROM DUAL;") { GenericStringTestCaseParser(validTestCase).getTestCase().tests[0].body }
            expect("This isn't really a test") { GenericStringTestCaseParser(validTestCase).getTestCase().tests[0].description }
        }
    }

    context("invalid") {
        it("fails for procedure test case") {
            { GenericStringTestCaseParser(ResourceHelper.getTestCaseByName("degenerate")) } shouldThrow BaseStringTestCaseParser.InvalidTestCaseFormat::class
        }

        it("fails for test cases with inappropriate hooks") {

        }
    }

    context("complete") {
        val validTestCase = ResourceHelper.getTestCaseByName("projectBased/genericSetupTeardown")

        it("parses without errors") {
            { GenericStringTestCaseParser(validTestCase).getTestCase() } shouldNotThrow AnyException
        }

        it("parses correctly") {
            val testCase = GenericStringTestCaseParser(validTestCase).getTestCase()
            expect(0) { testCase.hooks.beforeEachRow.size }
            expect(0) { testCase.hooks.afterEachRow.size }
            expect(3) { testCase.hooks.beforeEachTest.size }
            expect("With tunas drink tea") { testCase.hooks.beforeEachTest[2] }
            expect(2) { testCase.hooks.afterEachTest.size }
            expect("Remove transactions") { testCase.hooks.afterEachTest[0] }
            expect(3) { testCase.hooks.beforeAll.size }
            expect("Setup client") { testCase.hooks.beforeAll[0] }
            expect(2) { testCase.hooks.afterAll.size }
            expect("Remove clients") { testCase.hooks.afterAll[0] }
            expect("Example") { testCase.description }
            expect(2) { testCase.tests.size }
            expect(true) { testCase.tests[1].body.contains("NUKLL;") }
            expect("Contains a syntax error") { testCase.tests[1].description }
        }
    }
})
