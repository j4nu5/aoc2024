package dev.sinhak.aoc2024.day05

import println
import readInput
import java.lang.Long.parseLong

fun main() {
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143L)
    check(part2(testInput) == 0L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val orderingRulesInput = mutableListOf<String>()
    var lineNumber = 0
    while (lineNumber < input.size) {
        val line = input[lineNumber++]
        if (line.trim().isEmpty()) {
            break
        }

        orderingRulesInput.add(line)
    }
    val rules = parseRules(orderingRulesInput)

    var result = 0L
    while (lineNumber < input.size) {
        val line = input[lineNumber++]
        if (line.isEmpty()) {
            break
        }

        val pageNumbers = parsePageNumbers(line)
        if (isUpdateValid(rules, pageNumbers)) {
            result += getMiddlePageNumber(pageNumbers)
        }
    }

    return result
}

fun getMiddlePageNumber(pages: List<Long>): Long {
    if (pages.isEmpty()) {
        return 0
    }

    return pages[pages.size / 2]
}

fun isUpdateValid(rules: Map<Long, Set<Long>>, pages: List<Long>): Boolean {
    for (i in 0..<pages.size) {
        val prevPage = pages[i]
        for (j in i + 1..<pages.size) {
            val followingPage = pages[j]

            val isRuleInvalid = rules[followingPage]?.contains(prevPage) == true
            if (isRuleInvalid) {
                return false
            }
        }
    }

    return true
}

fun parsePageNumbers(input: String): List<Long> {
    return input.split(",").map { parseLong(it) }
}

fun parseRules(input: List<String>): Map<Long, Set<Long>> {
    val rules = HashMap<Long, HashSet<Long>>()
    for (rule in input) {
        val pages = rule.split("|")
        check(pages.size == 2)
        val prevPage: Long = parseLong(pages[0])
        val followingPage: Long = parseLong(pages[1])

        if (!rules.contains(prevPage)) {
            rules.put(prevPage, HashSet<Long>())
        }
        rules[prevPage]?.add(followingPage)
    }

    return rules
}

fun part2(input: List<String>): Long {
    return 0L
}
