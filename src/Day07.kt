package dev.sinhak.aoc2024.day07

import println
import readInput
import kotlin.time.measureTimedValue

data class Equation(
    val target: Long,
    val operands: List<Long>,
)

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("3: 3")) == 3L)
    check(part1(listOf("9876: 6 6 823")) == 9876L)
    check(part1(listOf("4: 1 1 4 2 3")) == 0L)

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()

    println("\nBenchmarks -\n")
    benchmark(curry(::part2, input), "BFS with pruning")
}

fun benchmark(solution: () -> Long, description: String) {
    val (result, time) = measureTimedValue { solution() }
    println("$description: $time (Result = $result)")
}

fun curry(solution: (List<String>) -> Long, input: List<String>): () -> Long {
    return fun(): Long {
        return solution(input)
    }
}

fun part1(input: List<String>): Long {
    var totalCalibrationResult = 0L

    for (line in input) {
        val equation: Equation = parse(line)
        if (canBeSatisfied(equation, ::findPossibleEvaluationsWithAddAndMultiplication)) {
            totalCalibrationResult += equation.target
        }
    }

    return totalCalibrationResult
}

fun part2(input: List<String>): Long {
    var totalCalibrationResult = 0L

    for (line in input) {
        val equation: Equation = parse(line)
        if (canBeSatisfied(equation, ::findPossibleEvaluationsWithAddMultiplicationAndConcat)) {
            totalCalibrationResult += equation.target
        }
    }

    return totalCalibrationResult
}

fun canBeSatisfied(
    equation: Equation,
    findPossibleEvaluations: (Long, Long) -> List<Long>,
): Boolean {
    var possibleTargets = mutableListOf<Long>()

    if (equation.operands.isEmpty()) {
        return equation.target == 0L
    }

    if (equation.operands[0] == equation.target) {
        return true
    }

    possibleTargets.add(equation.operands[0])
    for (i in 1..<equation.operands.size) {
        val operand = equation.operands[i]
        val newPossibleTargets = mutableListOf<Long>()

        for (target in possibleTargets) {
            val newTargets: List<Long> = findPossibleEvaluations(target, operand)
            newPossibleTargets.addAll(newTargets.filter { !shouldBePruned(it, equation.target) })
        }

        possibleTargets = newPossibleTargets
    }

    return possibleTargets.contains(equation.target)
}

fun findPossibleEvaluationsWithAddAndMultiplication(target: Long, operand: Long): List<Long> {
    return listOf(target + operand, target * operand)
}

fun findPossibleEvaluationsWithAddMultiplicationAndConcat(
    target: Long, operand: Long
): List<Long> {
    return listOf(target + operand, target * operand, concat(target, operand))
}

fun concat(a: Long, b: Long): Long {
    var result = a
    b.toString().forEach { ch ->
        result = result * 10 + (ch - '0').toLong()
    }
    return result
}

fun shouldBePruned(possibleTarget: Long, target: Long): Boolean {
    return possibleTarget > target
}

fun parse(input: String): Equation {
    val targetAndOperandsSplit = input.split(":")
    check(targetAndOperandsSplit.size == 2)
    val target = targetAndOperandsSplit[0].toLong()

    val operators: List<Long> = targetAndOperandsSplit[1].trim().split(" ").map { it.toLong() }
    return Equation(target, operators)
}
