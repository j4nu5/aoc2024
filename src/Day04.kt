package dev.sinhak.aoc2024.day04

import println
import readInput

fun main() {
    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 0L)
    check(part2(testInput) == 0L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    var numMatches = 0L
    for (i in 0..<input.size) {
        for (j in 0..<input[i].length) {
            numMatches += findMatch(input, "XMAS", i, j)
        }
    }

    return numMatches
}

fun part2(input: List<String>): Long {
    return 0L
}

/** Returns the number of matches we can find for [SEARCH_STRING] in [input] starting at (row, col)
 * with matching in [SEARCH_STRING] starting at [index] */
fun findMatch(input: List<String>, searchString: String, row: Int, col: Int, index: Int = 0): Int {
    if (index >= searchString.length) {
        return 0
    }
    val searchChar: Char = searchString[index]

    if (row < 0 || row >= input.size) {
        return 0
    }

    if (col < 0 || col >= input[row].length) {
        return 0
    }

    if (input[row][col] != searchChar) {
        return 0
    }

    if (index == searchString.length - 1) {
        return 1
    }

    var numMatches = 0
    for (rowDelta in -1..1) {
        for (colDelta in -1..1) {
            numMatches += findMatch(input, searchString, row + rowDelta, col + colDelta, rowDelta, colDelta, index + 1)
        }
    }

    return numMatches
}

/** Returns the number of matches we can find for [searchString] in [input] starting at (row, col)
 * with matching in [searchString] starting at [index] - while searching in a fixed direction given
 * by (rowDelta, colDelta). */
fun findMatch(
    input: List<String>, searchString: String, row: Int, col: Int, rowDelta: Int, colDelta: Int, index: Int
): Int {
    if (index >= searchString.length) {
        return 0
    }
    val searchChar: Char = searchString[index]

    if (row < 0 || row >= input.size) {
        return 0
    }

    if (col < 0 || col >= input[row].length) {
        return 0
    }

    if (input[row][col] != searchChar) {
        return 0
    }

    if (index == searchString.length - 1) {
        return 1
    }

    return findMatch(input, searchString, row + rowDelta, col + colDelta, rowDelta, colDelta, index + 1)
}
