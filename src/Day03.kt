package dev.sinhak.aoc2024.day03

import println
import readInput
import java.lang.Long.parseLong

enum class Operators {
    MUL, DO, DO_NOT,
}

data class Instruction(
    val operator: Operators,
    val operands: Pair<Long, Long>?,
)

fun main() {
    check(part1(listOf("mul(4*")) == 0L)
    check(part1(listOf("mul(123,4)")) == 492L)
    check(part1(listOf("mul(123345,4)")) == 0L)
    check(part1(listOf("mul(01,4)")) == 4L)
    check(part1(listOf("xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))")) == 161L)

    check(part2(listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")) == 48L)

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 322L)
    check(part2(testInput) == 209L)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val instructions: List<Instruction> = scanAndParseInput(input)

    var result = 0L
    instructions.forEach { instruction ->
        if (instruction.operator == Operators.MUL) {
            result += evaluateMultiplication(instruction)
        }
    }

    return result
}

fun part2(input: List<String>): Long {
    val instructions: List<Instruction> = scanAndParseInput(input)

    var result = 0L
    var isMultiplicationEnabled = true
    instructions.forEach { instruction ->
        when (instruction.operator) {
            Operators.MUL -> {
                if (isMultiplicationEnabled) {
                    result += evaluateMultiplication(instruction)
                }
            }

            Operators.DO -> {
                isMultiplicationEnabled = true
            }

            Operators.DO_NOT -> {
                isMultiplicationEnabled = false
            }
        }
    }

    return result
}

fun evaluateMultiplication(instruction: Instruction): Long {
    check(instruction.operator == Operators.MUL)
    return (instruction.operands?.first ?: 0L) * (instruction.operands?.second ?: 0L)
}

fun scanAndParseInput(input: List<String>): List<Instruction> {
    val scanner = Regex("""mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\)""")

    return input.flatMap { memoryLine ->
        scanner.findAll(memoryLine).map { parse(it.groupValues) }
    }
}

fun parse(regexGroupMatch: List<String>): Instruction {
    check(regexGroupMatch.isNotEmpty())
    when {
        regexGroupMatch[0].startsWith("mul") -> {
            check(regexGroupMatch.size == 3)
            return Instruction(
                operator = Operators.MUL,
                operands = Pair(parseLong(regexGroupMatch[1]), parseLong(regexGroupMatch[2]))
            )
        }

        regexGroupMatch[0].startsWith("don't") -> {
            return Instruction(
                operator = Operators.DO_NOT,
                operands = null,
            )
        }

        regexGroupMatch[0].startsWith("do") -> {
            return Instruction(
                operator = Operators.DO,
                operands = null,
            )
        }

        else -> {
            throw IllegalArgumentException("Unknown operator found")
        }
    }
}
