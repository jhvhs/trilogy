package io.pivotal.trilogy.testcase

import io.pivotal.trilogy.ResourceHelper
import org.jetbrains.spek.api.Spek
import kotlin.test.assertFails
import kotlin.test.expect

class StringTestCaseParserTests : Spek ({

    describe("degenerate") {
        val validTestCase = ResourceHelper.getTestCaseByName("degenerate")

        it("succeeds with a valid test case") {
            StringTestCaseParser(validTestCase)
        }

        it("gets a valid test case name") {
            val testCaseParser = StringTestCaseParser(validTestCase)
            expect("DEGENERATE") { testCaseParser.getTestCase().procedureName }
        }

        it("gets the test case description") {
            val testCaseParser = StringTestCaseParser(validTestCase)
            expect("Test case description") { testCaseParser.getTestCase().description }
        }

        it("parses the test") {
            val header = listOf("PARAM1", "PARAM2", "=ERROR=")
            val values = listOf(
                    listOf("FOO", "12", ""),
                    listOf("__NULL__", "0", ""),
                    listOf("BAR", "-18", ""),
                    listOf("", "12", "")
            )
            val arguments = TestArgumentTable(header, values)
            val test = TrilogyTest("Test description", arguments, emptyList())

            expect(test) { StringTestCaseParser(validTestCase).getTestCase().tests.first() }
        }

    }

    describe("multiple tests") {
        val validTestCase = ResourceHelper.getTestCaseByName("multiple/shouldPass")
        val testCase = StringTestCaseParser(validTestCase).getTestCase()

        it("should return two tests") {
            expect(2) { testCase.tests.count() }
        }

        it("should return empty fixture hook lists") {
            expect(true) { testCase.hooks.beforeAll.isEmpty() }
            expect(true) { testCase.hooks.beforeEachRow.isEmpty() }
            expect(true) { testCase.hooks.beforeEachTest.isEmpty() }
            expect(true) { testCase.hooks.afterAll.isEmpty() }
            expect(true) { testCase.hooks.afterEachRow.isEmpty() }
            expect(true) { testCase.hooks.afterEachTest.isEmpty() }
        }
    }

    describe("fixture hooks") {
        val testCase = ResourceHelper.getTestCaseByName("projectBased/setupTeardown")
        val testCaseHooks = StringTestCaseParser(testCase).getTestCase().hooks

        it("should extract before all hook names") {
            val beforeAllHooks = testCaseHooks.beforeAll
            expect(3) { beforeAllHooks.count() }
            expect("Setup client") { beforeAllHooks.first() }
            expect("Ships reproduce with xray vision") { beforeAllHooks[1] }
            expect("With melons drink maple syrup") { beforeAllHooks.last() }
        }

        it("should extract before each test hook names") {
            val beforeEachTestHooks = testCaseHooks.beforeEachTest
            expect(3) { beforeEachTestHooks.count() }
            expect("Set client balance") { beforeEachTestHooks.first() }
            expect("Grace life and passion") { beforeEachTestHooks[1] }
            expect("With tunas drink tea") { beforeEachTestHooks.last() }
        }

        it("should extract before each row hook names") {
            val beforeEachRowHooks = testCaseHooks.beforeEachRow
            expect(2) { beforeEachRowHooks.count() }
            expect("Contencio flavum vita est") { beforeEachRowHooks.first() }
            expect("Everyone just loves the fierceness of chicken cheesecake flavord with cumin.") { beforeEachRowHooks[1] }
        }

        it("should extract after all hook names") {
            val afterAllHooks = testCaseHooks.afterAll
            expect(2) { afterAllHooks.count() }
            expect("Remove clients") { afterAllHooks.first() }
            expect("Fraticinidas ire") { afterAllHooks.last() }
        }

        it("should extract after each test hook names") {
            val afterEachTestHooks = testCaseHooks.afterEachTest
            expect(2) { afterEachTestHooks.count() }
            expect("Remove transactions") { afterEachTestHooks.first() }
            expect("Be mysterious") { afterEachTestHooks.last() }
        }

        it("should extract after each row hook names") {
            val afterEachRowHooks = testCaseHooks.afterEachRow
            expect(1) { afterEachRowHooks.count() }
            expect("Always solitary yearn the spiritual saint.") { afterEachRowHooks.first() }
        }
    }

    describe("Empty fixture hook sections") {
        val validTestCase = ResourceHelper.getTestCaseByName("projectBased/blankSetupTeardown")
        val testCaseHooks = StringTestCaseParser(validTestCase).getTestCase().hooks

        it("should return empty fixture hook lists") {
            expect(true) { testCaseHooks.beforeAll.isEmpty() }
            expect(true) { testCaseHooks.beforeEachTest.isEmpty() }
            expect(true) { testCaseHooks.beforeEachRow.isEmpty() }
            expect(true) { testCaseHooks.afterAll.isEmpty() }
            expect(true) { testCaseHooks.afterEachTest.isEmpty() }
            expect(true) { testCaseHooks.afterEachRow.isEmpty() }
        }

    }

    it("fails with invalid test case") {
        assertFails { StringTestCaseParser("") }
    }

    it("fails with empty test case description") {
        assertFails { StringTestCaseParser(ResourceHelper.getTestCaseByName("emptyDescription")).getTestCase() }
    }

    it("fails with empty function name") {
        assertFails { StringTestCaseParser(ResourceHelper.getTestCaseByName("emptyFunctionName")).getTestCase() }
    }


})