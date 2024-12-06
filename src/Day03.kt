package dev.sinhak.aoc2024.day03

import println
import readInput
import java.lang.Long.parseLong

data class Instruction(
    val operator: String = "MUL",
    val operands: Pair<Long, Long>,
)

fun main() {
    check(part1(listOf("mul(4*")) == 0L)
    check(part1(listOf("mul(123,4)")) == 492L)
    check(part1(listOf("mul(123345,4)")) == 0L)
    check(part1(listOf("mul(01,4)")) == 4L)

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 322L)
    check(part2(testInput) == 0L)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val instructions: List<Instruction> = scanAndParseInput(input)

    var result = 0L
    instructions.forEach { instruction ->
        check("MUL".equals(instruction.operator))
        result += instruction.operands.first * instruction.operands.second
    }

    return result
}

fun part2(input: List<String>): Long {
    return 0L
}

fun scanAndParseInput(input: List<String>): List<Instruction> {
    val scanner = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

    return input.flatMap { memoryLine ->
        scanner.findAll(memoryLine).map { parse(it.groupValues) }
    }
}

fun parse(regexGroupMatch: List<String>): Instruction {
    check(regexGroupMatch.size == 3)
    return Instruction(operands = Pair(parseLong(regexGroupMatch[1]), parseLong(regexGroupMatch[2])))
}
