#set( $Code = "bar" )
package dev.sinhak.aoc2024.day$Day

import println
import readInput

fun main() {
    check(part1(listOf("3 5")) == 0L)

    val testInput = readInput("Day${Day}_test")
    check(part1(testInput) == 0L)
    check(part2(testInput) == 0L)

    val input = readInput("Day${Day}")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    return 0L
}

fun part2(input: List<String>): Long {
    return 0L
}
