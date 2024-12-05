package dev.sinhak.aoc2024.day02

import println
import readInput
import java.lang.Long.parseLong
import kotlin.math.abs

fun main() {
    check(part1(listOf("7 6 4 2 1")) == 1)

    check(part2(listOf("7 8 4 2 1")) == 1)
    check(part2(listOf("7 8 3 2 1")) == 0)
    check(part2(listOf("7 8 9 8 7")) == 0)

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Int {
    val reports = parseInput(input)
    return countSafeReports(reports)
}

fun part2(input: List<String>): Int {
    val reports = parseInput(input)
    return countSafeReportsWithTolerance(reports)
}

fun countSafeReports(reports: List<List<Long>>): Int {
    return reports.filter { isSafeReport(it) }.size
}

fun countSafeReportsWithTolerance(reports: List<List<Long>>): Int {
    return reports.filter { isSafeReport(it) || isSafeReportWithTolerance(it) }.size
}

fun isSafeReport(report: List<Long>): Boolean {
    if (report.size < 2) {
        return true
    }

    val isIncreasing: Boolean = report[1] > report[0]
    for (i in 0..(report.size - 2)) {
        if (!areLevelsSafe(report[i], report[i + 1], isIncreasing)) {
            return false
        }
    }

    return true
}

fun isSafeReportWithTolerance(report: List<Long>): Boolean {
    for (i in 0..(report.size - 1)) {
        // Consider the report with element at i removed.
        val leftList = report.subList(0, i)
        val rightList = report.subList(i + 1, report.size)

        if (canBeJoined(leftList, rightList)) {
            return true
        }
    }

    return false
}

fun canBeJoined(
    list1: List<Long>, list2: List<Long>
): Boolean {
    return isSafeReport(list1 + list2)
}

fun areLevelsSafe(level1: Long, level2: Long, shouldIncrease: Boolean): Boolean {
    if (shouldIncrease && level1 > level2) {
        return false
    }

    if (!shouldIncrease && level1 < level2) {
        return false
    }

    return isDifferenceSafe(level1, level2)
}

fun isDifferenceSafe(a: Long, b: Long): Boolean {
    val diff = abs(a - b)
    return diff >= 1 && diff <= 3
}

fun parseInput(input: List<String>): List<List<Long>> {
    val parsedInput = mutableListOf<List<Long>>()

    input.forEach { line ->
        val parsedLine = mutableListOf<Long>()
        val listItems = line.trim().split("\\s+".toRegex())
        listItems.forEach { number ->
            parsedLine.add(parseLong(number))
        }

        parsedInput.add(parsedLine)
    }

    return parsedInput
}
