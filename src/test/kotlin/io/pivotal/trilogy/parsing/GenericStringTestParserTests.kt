package io.pivotal.trilogy.parsing

import io.pivotal.trilogy.parsing.exceptions.test.MissingAssertionBody
import io.pivotal.trilogy.parsing.exceptions.test.MissingAssertionDescription
import io.pivotal.trilogy.parsing.exceptions.test.MissingBody
import io.pivotal.trilogy.parsing.exceptions.test.MissingDescription
import io.pivotal.trilogy.test_helpers.ResourceHelper
import io.pivotal.trilogy.test_helpers.shouldContain
import io.pivotal.trilogy.test_helpers.shouldNotThrow
import io.pivotal.trilogy.test_helpers.shouldThrow
import org.amshove.kluent.AnyException
import org.jetbrains.spek.api.Spek
import kotlin.test.expect


class GenericStringTestParserTests : Spek({
    context("minimal") {
        val testString = ResourceHelper.getTestByName("genericMinimal")

        it("can be read") {
            { GenericStringTestParser(testString).getTest() } shouldNotThrow AnyException
        }

        it("reads the test description") {
            expect("Test description") { GenericStringTestParser(testString).getTest().description }
        }

        it("reads the test") {
            expect("BEGIN\n  NULL;\nEND;") { GenericStringTestParser(testString).getTest().body }
        }

        it("reads the assertions") {
            expect(0) { GenericStringTestParser(testString).getTest().assertions.size }
        }

    }

    context("minimal with SQL header") {
        val testString = ResourceHelper.getTestByName("genericMinimalSql")

        it("can be read") {
            { GenericStringTestParser(testString).getTest() } shouldNotThrow AnyException
        }

        it("reads the test description") {
            expect("Winds scream with halitosis!") { GenericStringTestParser(testString).getTest().description }
        }

        it("reads the test") {
            expect("BEGIN\n  NULL;\nEND;") { GenericStringTestParser(testString).getTest().body }
        }

        it("reads the assertions") {
            expect(0) { GenericStringTestParser(testString).getTest().assertions.size }
        }
    }

    context("full test") {
        val testString = ResourceHelper.getTestByName("genericWithTestHooks")

        it("can be read") {
            { GenericStringTestParser(testString).getTest() } shouldNotThrow AnyException
        }

        it("reads the test description") {
            expect("You have to fail, and believe booda-hood by your growing.") { GenericStringTestParser(testString).getTest().description }
        }

        it("reads the test") {
            expect("BEGIN\n  NULL;\nEND;") { GenericStringTestParser(testString).getTest().body }
        }

        it("reads the assertions") {
            expect(3) { GenericStringTestParser(testString).getTest().assertions.size }
        }

        it("assigns the assertion contents") {
            expect("With herrings drink fish sauce.\n  NOODLES;") { GenericStringTestParser(testString).getTest().assertions.first().body }
        }
    }

    context("invalid test") {
        val testString = ResourceHelper.getTestByName("genericInvalid")

        it("can be read") {
            { GenericStringTestParser(testString).getTest() } shouldThrow MissingBody::class
        }
    }

    context("minimal with test hooks") {
        val testString = ResourceHelper.getTestByName("genericMinimalWithTestHooks")

        it("can be read") {
            { GenericStringTestParser(testString) } shouldNotThrow AnyException
        }

        it("assigns before hook") {
            expect(2) { GenericStringTestParser(testString).getTest().hooks.before.size }
        }

        it("sets before hook contents") {
            expect("Sunt fluctuies acquirere secundus, germanus quadraes.") { GenericStringTestParser(testString).getTest().hooks.before.first() }
            expect("Rhubarb combines greatly with delicious peanut butter.") { GenericStringTestParser(testString).getTest().hooks.before[1] }
        }

        it("assigns after hooks") {
            expect(1) { GenericStringTestParser(testString).getTest().hooks.after.size }
        }

        it("sets after hook contents") {
            expect("One magical death i give you: develop each other.") { GenericStringTestParser(testString).getTest().hooks.after.first() }
        }
    }
    context("with assertions") {
        val testString = ResourceHelper.getTestByName("genericWithAssertions")

        it("reads 2 assertions") {
            expect(2) { GenericStringTestParser(testString).getTest().assertions.size }
        }

        it("reads assertion descriptions") {
            expect("Assertion description 1") { GenericStringTestParser(testString).getTest().assertions[0].description }
            expect("Assertion description 2") { GenericStringTestParser(testString).getTest().assertions[1].description }
        }

        it("reads assertion bodies") {
            GenericStringTestParser(testString).getTest().assertions[0].body shouldContain "l_count NUMBER"
            GenericStringTestParser(testString).getTest().assertions[1].body shouldContain "alt_count NUMBER"
        }
    }

    it("requires a test body") {
        { GenericStringTestParser("## TEST\nStigma at the alpha quadrant") } shouldThrow MissingBody::class
    }

    it("cannot contain a data section") {
        { GenericStringTestParser("## TEST\nBlah\n### DATA\n| P1 |\n|----|\n| 12 |\n") } shouldThrow MissingBody::class
    }

    it("requires a test description") {
        { GenericStringTestParser("## TEST\n```\nBEGIN\nNULL\nEND\n```") } shouldThrow MissingDescription::class
    }

    it("requires an assertion name") {
        { GenericStringTestParser("## TEST\nBlah\n```\nfoo\n```\n### ASSERTIONS\n#### SQL\n```\nbar\n```").getTest() } shouldThrow MissingAssertionDescription::class
    }

    it("requires the assertion body") {
        { GenericStringTestParser("## TEST\nBlah\n```\nfoo\n```\n### ASSERTIONS\n#### SQL\nbar```\n\n```").getTest() } shouldThrow MissingAssertionBody::class
        { GenericStringTestParser("## TEST\nBlah\n```\nfoo\n```\n### ASSERTIONS\n#### SQL\nbar").getTest() } shouldThrow MissingAssertionBody::class
    }

})