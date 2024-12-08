package dev.sinhak.aoc2024.day01

import println
import readInput
import java.lang.Long.parseLong
import kotlin.math.abs

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("3 5")) == 2L)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11L)
    check(part2(testInput) == 31L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val parsedInput = parseInput(input)
    val locations1 = parsedInput.first.sorted()
    val locations2 = parsedInput.second.sorted()
    return findDifference(locations1, locations2)
}

fun part2(input: List<String>): Long {
    val parsedInput = parseInput(input)
    val locations1 = parsedInput.first.sorted()
    val locations2 = parsedInput.second.sorted()
    return findSimilarity(locations1, locations2)
}

fun findSimilarity(list1: List<Long>, list2: List<Long>): Long {
    var similarity = 0L

    list1.forEach { location ->
        similarity += (location * findFrequency(location, list2))
    }

    return similarity
}

fun findFrequency(input: Long, sortedList: List<Long>): Long {
    val leftIndex = binarySearchLeft(input, sortedList)
    val rightIndex = binarySearchRight(input, sortedList)

    if (leftIndex < 0) {
        check(rightIndex < 0)
        return 0
    }

    check(leftIndex <= rightIndex)
    check(sortedList[leftIndex] == input)
    check(sortedList[rightIndex] == input)

    return rightIndex - leftIndex + 1L
}

fun binarySearchLeft(needle: Long, haystack: List<Long>) : Int {
    val index = binarySearch(haystack) { it >= needle }

    if (index < 0) {
        return -1
    }

    if (haystack[index] != needle) {
        return -1
    }

    return index
}

fun binarySearchRight(needle: Long, haystack: List<Long>) : Int {
    val index = binarySearch(haystack) { it > needle }

    if (index <= 0) {
        return -1
    }

    if (haystack[index - 1] != needle) {
        return -1
    }

    return index - 1
}

// Returns the first index in [haystack] where predicate is true, -1 if such an index can't be found.
// Assuming that haystack and a bunch of false predicates, followed by true predicates.
fun binarySearch(haystack: List<Long>, predicate: (Long) -> Boolean) : Int {
    if (haystack.isEmpty()) {
        return -1
    }

    var start = 0
    var end = haystack.size - 1

    while (start < end) {
        val mid = start + (end - start) / 2

        if (predicate(haystack[mid])) {
            end = mid
        } else {
            start = mid + 1
        }
    }

    if (predicate(haystack[start])) {
        return start
    }

    return -1
}

fun findDifference(list1: List<Long>, list2: List<Long>): Long {
    check(list1.size == list2.size)
    val size = list1.size

    var diff = 0L
    for (i in 0..size - 1) {
        diff += abs(list1[i] - list2[i])
    }

    return diff
}

fun parseInput(input: List<String>): Pair<List<Long>, List<Long>> {
    val list1 = mutableListOf<Long>()
    val list2 = mutableListOf<Long>()

    input.forEach { line ->
        val listItems = line.trim().split("\\s+".toRegex())
        if (listItems.size == 2) {
            list1.add(parseLong(listItems[0]))
            list2.add(parseLong(listItems[1]))
        }
    }

    return Pair(list1, list2)
}
