package dev.sinhak.aoc2024.day06

import println
import readInput

class PuzzleMap private constructor(
    private val map: ArrayList<CharArray>,
    private val visitCount: ArrayList<ArrayList<Int>>
) {
    var numGuardPositions: Long = 0L
        private set
        get() {
            var numPositions = 0L
            for (row in map) {
                for (character in row) {
                    if (character == PATH_MARKER || guardIndicators.contains(character)) {
                        numPositions++
                    }
                }
            }
            return numPositions
        }
    private var currentGuardPosition = Pair<Int, Int>(-1, -1)

    companion object {
        const val PATH_MARKER = 'X'
        const val OBSTACLE = '#'

        const val FACING_UP = '^'
        const val FACING_DOWN = 'v'
        const val FACING_LEFT = '<'
        const val FACING_RIGHT = '>'

        val guardIndicators = listOf<Char>(FACING_UP, FACING_DOWN, FACING_RIGHT, FACING_LEFT)

        fun parse(input: List<String>): PuzzleMap {
            val map = ArrayList<CharArray>()
            val visitCount = ArrayList<ArrayList<Int>>()

            for (line in input) {
                map.add(line.toCharArray())
                visitCount.add(arrayListOf())
                line.forEach { ch ->
                    visitCount[visitCount.size - 1].add(0)
                }
            }

            return PuzzleMap(map, visitCount)
        }
    }

    fun floodFillGuardMovement() {
        findCurrentGuardPosition()
        while (moveGuard()) {}
    }

    private fun moveGuard(): Boolean {
        val guardRow = currentGuardPosition.first
        val guardCol = currentGuardPosition.second

        if (guardRow < 0 || guardRow >= map.size) {
            return false
        }

        if (guardCol < 0 || guardCol >= map[guardRow].size) {
            return false
        }

        check(guardIndicators.contains(map[guardRow][guardCol]))
        when (map[guardRow][guardCol]) {
            FACING_UP -> {
                if (isObstacle(guardRow - 1, guardCol)) {
                    map[guardRow][guardCol] = FACING_RIGHT
                    visitCount[guardRow][guardCol]++
                    return true
                }

                if (isValidMovementPosition(guardRow - 1, guardCol)) {
                    map[guardRow][guardCol] = PATH_MARKER
                    visitCount[guardRow][guardCol]++
                    currentGuardPosition = Pair(guardRow - 1, guardCol)
                    map[currentGuardPosition.first][currentGuardPosition.second] = FACING_UP
                    return true
                }
            }

            FACING_DOWN -> {
                if (isObstacle(guardRow + 1, guardCol)) {
                    map[guardRow][guardCol] = FACING_LEFT
                    visitCount[guardRow][guardCol]++
                    return true
                }

                if (isValidMovementPosition(guardRow + 1, guardCol)) {
                    map[guardRow][guardCol] = PATH_MARKER
                    visitCount[guardRow][guardCol]++
                    currentGuardPosition = Pair(guardRow + 1, guardCol)
                    map[currentGuardPosition.first][currentGuardPosition.second] = FACING_DOWN
                    return true
                }
            }

            FACING_LEFT -> {
                if (isObstacle(guardRow, guardCol - 1)) {
                    map[guardRow][guardCol] = FACING_UP
                    visitCount[guardRow][guardCol]++
                    return true
                }

                if (isValidMovementPosition(guardRow, guardCol - 1)) {
                    map[guardRow][guardCol] = PATH_MARKER
                    visitCount[guardRow][guardCol]++
                    currentGuardPosition = Pair(guardRow, guardCol - 1)
                    map[currentGuardPosition.first][currentGuardPosition.second] = FACING_LEFT
                    return true
                }
            }

            FACING_RIGHT -> {
                if (isObstacle(guardRow, guardCol + 1)) {
                    map[guardRow][guardCol] = FACING_DOWN
                    visitCount[guardRow][guardCol]++
                    return true
                }

                if (isValidMovementPosition(guardRow, guardCol + 1)) {
                    map[guardRow][guardCol] = PATH_MARKER
                    visitCount[guardRow][guardCol]++
                    currentGuardPosition = Pair(guardRow, guardCol + 1)
                    map[currentGuardPosition.first][currentGuardPosition.second] = FACING_RIGHT
                    return true
                }
            }
        }

        return false
    }

    private fun isObstacle(row: Int, col: Int): Boolean {
        if (row < 0 || row >= map.size) {
            return false
        }

        if (col < 0 || col >= map[row].size) {
            return false
        }

        return map[row][col] == OBSTACLE
    }

    private fun isValidMovementPosition(row: Int, col: Int): Boolean {
        if (row < 0 || row >= map.size) {
            return false
        }

        if (col < 0 || col >= map[row].size) {
            return false
        }

        if (map[row][col] == OBSTACLE) {
            return false
        }

        if (map[row][col] == PATH_MARKER) {
            return visitCount[row][col] <= 4
        }

        return true
    }

    fun findCurrentGuardPosition() {
        for (i in 0..<map.size) {
            for (j in 0..<map[i].size) {
                val character = map[i][j]
                if (guardIndicators.contains(character)) {
                    currentGuardPosition = Pair(i, j)
                    return
                }
            }
        }
    }

    fun isGuardStuckInALoop(): Boolean {
        val guardRow = currentGuardPosition.first
        val guardCol = currentGuardPosition.second

        if (guardRow < 0 || guardRow >= map.size) {
            return false
        }

        if (guardCol < 0 || guardCol >= map[guardRow].size) {
            return false
        }

        check(guardIndicators.contains(map[guardRow][guardCol]))
        when (map[guardRow][guardCol]) {
            FACING_UP -> {
                val nextRow = guardRow - 1
                val nextCol = guardCol

                if (nextRow < 0 || nextRow >= map.size) {
                    return false
                }

                if (nextCol < 0 || nextCol >= map[nextRow].size) {
                    return false
                }
            }

            FACING_DOWN -> {
                val nextRow = guardRow + 1
                val nextCol = guardCol

                if (nextRow < 0 || nextRow >= map.size) {
                    return false
                }

                if (nextCol < 0 || nextCol >= map[nextRow].size) {
                    return false
                }
            }

            FACING_LEFT -> {
                val nextRow = guardRow
                val nextCol = guardCol - 1

                if (nextRow < 0 || nextRow >= map.size) {
                    return false
                }

                if (nextCol < 0 || nextCol >= map[nextRow].size) {
                    return false
                }
            }

            FACING_RIGHT -> {
                val nextRow = guardRow
                val nextCol = guardCol + 1

                if (nextRow < 0 || nextRow >= map.size) {
                    return false
                }

                if (nextCol < 0 || nextCol >= map[nextRow].size) {
                    return false
                }
            }
        }

        return true
    }

    fun tryBlocking(row: Int, col: Int): Boolean {
        if (isObstacle(row, col)) {
            return false
        }

        if (guardIndicators.contains(map[row][col])) {
            return false
        }

        map[row][col] = OBSTACLE
        return true
    }
}

fun main() {
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41L)
    check(part2(testInput) == 6L)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

fun part1(input: List<String>): Long {
    val puzzleMap = PuzzleMap.parse(input)
    puzzleMap.floodFillGuardMovement()
    val result = puzzleMap.numGuardPositions

    return result
}

fun part2(input: List<String>): Long {
    var numLoops = 0L
    for (i in 0..<input.size) {
        for (j in 0..<input[i].length) {
            val puzzleMap = PuzzleMap.parse(input)
            if (!puzzleMap.tryBlocking(i, j)) {
                continue
            }
            puzzleMap.floodFillGuardMovement()
            if (puzzleMap.isGuardStuckInALoop()) {
                numLoops++
            }
        }
    }

    return numLoops
}
