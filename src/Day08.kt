package dev.sinhak.aoc2024.day08

import println
import readInput

typealias Location = Pair<Int, Int>

fun main() {
    check(part1(listOf("a")) == 0L)
    check(part1(listOf("aa")) == 0L)
    check(part1(listOf(".aa.")) == 2L)
    check(part1(listOf(".aa")) == 1L)
    check(part1(listOf(".$$.")) == 0L)

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14L)
    check(part2(testInput) == 0L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val antiNodes: List<List<Int>> = findAntiNodesInInputMap(input)
    return countAntiNodes(antiNodes)
}

fun isAntenna(ch: Char): Boolean {
    return ch.isLetterOrDigit()
}

fun findAntiNodesInInputMap(input: List<String>): List<List<Int>> {
    val antiNodes: MutableList<MutableList<Int>> = mutableListOf()
    val antennaLocations: Map<Char, List<Location>> = findAntennas(input)

    for (i in 0..<input.size) {
        antiNodes.add(MutableList(input[i].length) { 0 })
    }

    for ((_, locations) in antennaLocations.entries) {
        findAntiNodesUsingLocations(locations).forEach {
            markOnMapIfExists(it, antiNodes)
        }
    }

    return antiNodes
}

fun markOnMapIfExists(
    location: Location, antiNodeMap: MutableList<MutableList<Int>>
) {
    val row = location.first
    val col = location.second
    if (row < 0 || row >= antiNodeMap.size) {
        return
    }

    if (col < 0 || col >= antiNodeMap[row].size) {
        return
    }

    antiNodeMap[row][col] = 1
}

fun findAntiNodesUsingLocations(locations: List<Location>): List<Location> {
    val antiNodes = mutableListOf<Location>()

    for (i in 0..<locations.size) {
        for (j in i + 1..<locations.size) {
            antiNodes.addAll(findAntiNodes(locations[i], locations[j]))
        }
    }

    return antiNodes
}

fun findAntiNodes(location1: Location, location2: Location): List<Location> {
    val antiNodes = mutableListOf<Location>()

    // a1.x - l1.x = l1.x - l2.x
    // a2.x - l2.x = l2.x - l1.x
    antiNodes.add(
        (2 * location1.first - location2.first) to (2 * location1.second - location2.second)
    )
    antiNodes.add(
        (2 * location2.first - location1.first) to (2 * location2.second - location1.second)
    )

    return antiNodes
}

fun findAntennas(input: List<String>): Map<Char, List<Location>> {
    val antennas = mutableMapOf<Char, MutableList<Location>>()

    for (i in 0..<input.size) {
        for (j in 0..<input[i].length) {
            val ch = input[i][j]
            if (isAntenna(ch)) {
                val locations = antennas.getOrPut(ch) { mutableListOf<Location>() }
                locations.add(i to j)
            }
        }
    }

    return antennas
}

fun countAntiNodes(antiNodes: List<List<Int>>): Long {
    var numAntiNodes = 0L

    for (row in antiNodes) {
        for (locationMarker in row) {
            if (locationMarker == 1) {
                numAntiNodes++
            }
        }
    }

    return numAntiNodes
}

fun part2(input: List<String>): Long {
    return 0L
}
