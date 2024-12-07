package dev.sinhak.aoc2024.day07

import println
import readInput
import java.math.BigInteger

data class Equation(
    val target: BigInteger,
    val operands: List<BigInteger>,
)

fun main() {
    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("3: 3")) == BigInteger.valueOf(3L))
    check(part1(listOf("9876: 6 6 823")) == BigInteger.valueOf(9876L))
    check(part1(listOf("4: 1 1 4 2 3")) == BigInteger.valueOf(0L))

    val testInput = readInput("Day07_test")
    check(part1(testInput) == BigInteger.valueOf(3749L))
    check(part2(testInput) == BigInteger.ZERO)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): BigInteger {
    var totalCalibrationResult = BigInteger.ZERO

    for (line in input) {
        val equation: Equation = parse(line)
        if (canBeSatisfied(equation)) {
            totalCalibrationResult += equation.target
        }
    }

    return totalCalibrationResult
}

fun part2(input: List<String>): BigInteger {
    return BigInteger.ZERO
}

fun canBeSatisfied(equation: Equation): Boolean {
    var possibleTargets = mutableListOf<BigInteger>()

    if (equation.operands.isEmpty()) {
        return equation.target == BigInteger.ZERO
    }

    if (equation.operands[0] == equation.target) {
        return true
    }

    possibleTargets.add(equation.operands[0])
    for (i in 1..<equation.operands.size) {
        val operand = equation.operands[i]
        val newPossibleTargets = mutableListOf<BigInteger>()

        for (target in possibleTargets) {
            val newTargets: List<BigInteger> = findPossibleEvaluations(target, operand)
            newPossibleTargets.addAll(newTargets.filter { !shouldBePruned(it, equation.target) })
        }

        possibleTargets = newPossibleTargets
    }

    return possibleTargets.contains(equation.target)
}

fun findPossibleEvaluations(target: BigInteger, operand: BigInteger): List<BigInteger> {
    return listOf(target + operand, target * operand)
}

fun shouldBePruned(possibleTarget: BigInteger, target: BigInteger): Boolean {
    return possibleTarget > target
}

fun parse(input: String): Equation {
    val targetAndOperandsSplit = input.split(":")
    check(targetAndOperandsSplit.size == 2)
    val target = targetAndOperandsSplit[0].toBigInteger()

    val operators: List<BigInteger> = targetAndOperandsSplit[1].trim().split(" ").map { it.toBigInteger() }
    return Equation(target, operators)
}
