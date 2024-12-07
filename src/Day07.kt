package dev.sinhak.aoc2024.day07

import println
import readInput
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.pow
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

    // Equation evaluation strategies.
    val bfsWithPruning = fun(equation: Equation): Boolean {
        return canBeSatisfied(equation, ::findPossibleEvaluationsWithAddMultiplicationAndConcat)
    }
    val bfsWithPruningAndOptAllocations = fun(equation: Equation): Boolean {
        return canBeSatisfiedWithOptAllocations(equation)
    }
    val aStar = fun(equation: Equation): Boolean {
        return canBeSatisfiedWithAStarSearch(equation)
    }

    println("\nBenchmarks -\n")
    benchmark("Original recipe - BFS with pruning", curry(input, fun(input: List<String>): Long {
        return part2(input, bfsWithPruning)
    }))
    benchmark(
        "Extra crunchy - BFS with pruning with optimized allocations", curry(input, fun(input: List<String>): Long {
            return part2(input, bfsWithPruningAndOptAllocations)
        })
    )
    benchmark(
        "Flaming hot - A Star search", curry(input, fun(input: List<String>): Long {
            return part2(input, aStar)
        })
    )
}

fun benchmark(description: String, solution: () -> Long) {
    val (result, time) = measureTimedValue { solution() }
    println("$description: $time (Result = $result)")
}

fun curry(input: List<String>, solution: (List<String>) -> Long): () -> Long {
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

// Same as above except that it accepts a custom evaluation function.
fun part2(input: List<String>, canEquationBeSatisfied: (Equation) -> Boolean): Long {
    var totalCalibrationResult = 0L

    for (line in input) {
        val equation: Equation = parse(line)
        if (canEquationBeSatisfied(equation)) {
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

fun canBeSatisfiedWithOptAllocations(
    equation: Equation,
): Boolean {
    // Assuming we won't overflow.
    val numPossibleTargets = 3.toDouble().pow(equation.operands.size - 1).toInt()
    val possibleTargets: ArrayList<Long> = ArrayList<Long>(numPossibleTargets)
    val newPossibleTargets: ArrayList<Long> = ArrayList<Long>(numPossibleTargets)

    if (equation.operands.isEmpty()) {
        return equation.target == 0L
    }

    possibleTargets.add(equation.operands[0])
    for (i in 1..<equation.operands.size) {
        val operand = equation.operands[i]

        for (target in possibleTargets) {
            val newTargets: List<Long> = findPossibleEvaluationsWithAddMultiplicationAndConcat(target, operand)
            newPossibleTargets.addAll(newTargets.filter { !shouldBePruned(it, equation.target) })
        }

        possibleTargets.clear()
        possibleTargets.addAll(newPossibleTargets)
        newPossibleTargets.clear()
    }

    return possibleTargets.contains(equation.target)
}

fun canBeSatisfiedWithAStarSearch(
    equation: Equation,
): Boolean {
    val solver = AStarSolver(equation)
    return solver.isSolutionPossible()
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

class AStarSolver(private val equation: Equation) {
    // We are going to use A Star search to see if equation can be solved.
    // The idea is to construct a search tree of possible evaluation results by iteratively moving
    // through the list of operands in equation.
    // The halting criteria will be either finding an evaluation result as a leaf node (note the
    // difference from standard A Star implementation - we cannot end the search early) - or by
    // exhausting the search space.

    // Represents a node in the search space.
    inner class Node(
        // Value / result of the evaluation which produced this node.
        val value: Long,

        // Level of node in the search tree. Range - [0, number of operands), where 0 is root.
        val level: Int,
    ) : Comparable<Node> {
        fun isLeafNode(): Boolean {
            return level == (equation.operands.size - 1)
        }

        fun isValidSolution(): Boolean {
            return (value == equation.target) && isLeafNode()
        }

        override fun compareTo(other: Node): Int {
            val cmp = estimatedCost() - other.estimatedCost()
            return when {
                cmp == 0L -> {
                    0
                }

                cmp < 0L -> {
                    -1
                }

                else -> {
                    1
                }
            }
        }

        private fun estimatedCost(): Long {
            // Lower the better.

            // Unlike regular A star search, we are not trying to the shortest path. We have to
            // end up at the leaf node anyway. However, it may be useful to prune out
            // non-promising search branches as early as possible.

            // If delta is our heuristic value -
            // Low delta, low level = Good.
            // Low delta, high level = Perfect.
            // High delta, low level = OK.
            // High delta, high level = Bad.

            // TODO: Find a better formula to represent the above idea.
            return level + heuristic()
        }

        private fun heuristic(): Long {
            return abs(value - equation.target)
        }
    }

    // Represents the frontier of the search space.
    val frontier = PriorityQueue<Node>()

    fun isSolutionPossible(): Boolean {
        if (equation.operands.isEmpty()) {
            return equation.target == 0L
        }

        frontier.add(
            Node(
                value = equation.operands[0],
                level = 0,
            )
        )
        while (frontier.isNotEmpty()) {
            val currentNode = frontier.poll()

            if (currentNode.isValidSolution()) {
                return true
            }

            if (currentNode.isLeafNode()) {
                continue
            }

            // We need to try the "next" operand on [currentNode].
            val operand = equation.operands[currentNode.level + 1]
            val newPossibleTargets: List<Long> =
                findPossibleEvaluationsWithAddMultiplicationAndConcat(currentNode.value, operand)

            frontier.addAll(newPossibleTargets.filter { !shouldBePruned(it, equation.target) }.map {
                Node(
                    value = it,
                    level = currentNode.level + 1,
                )
            })
        }

        return false
    }
}
