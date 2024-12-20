package dev.sinhak.aoc2024.day09

import println
import readInput

const val FREE_SPACE_MARKER = -1

data class FreeSpaceBlock(
    var size: Int,
    var ptr: Int,
)

data class FileBlock(
    val id: Int,
    val size: Int,
    var ptr: Int,
)

data class DiskLayout(
    val layout: ArrayList<Int>,
    val freeSpaceBlocks: ArrayList<FreeSpaceBlock>,
    val fileBlocks: ArrayList<FileBlock>,
)

fun main() {
    check(part1(listOf("101")) == 1L)
    check(part2(listOf("101")) == 1L)
    check(part2(listOf("0322302")) == 58L)

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

fun part2(input: List<String>): Long {
    val disk: DiskLayout = constructDiskLayout(input[0])
    defragBlocks(disk)
    return checksum(disk.layout)
}

fun part1(input: List<String>): Long {
    val disk: DiskLayout = constructDiskLayout(input[0])
    defrag(disk)
    return checksum(disk.layout)
}

fun constructDiskLayout(input: String): DiskLayout {
    var diskSize: Int = 0
    for (ch in input) {
        diskSize += (ch - '0')
    }
    println("Debug: Allocating $diskSize for disk size")

    val layout = ArrayList<Int>()
    val freeSpaceBlocks = arrayListOf<FreeSpaceBlock>()
    val fileBlocks = arrayListOf<FileBlock>()
    var fileId = -1
    for (i in 0..<input.length) {
        val size = (input[i] - '0')
        val isFileBlock = (i % 2 == 0)
        if (isFileBlock) {
            fileId++
        }
        val diskContent = if (isFileBlock) fileId else FREE_SPACE_MARKER

        if (size <= 0) {
            continue
        }

        if (diskContent == FREE_SPACE_MARKER) {
            freeSpaceBlocks.add(FreeSpaceBlock(size = size, ptr = layout.size))
        } else {
            fileBlocks.add(FileBlock(id = fileId, size = size, ptr = layout.size))
        }

        repeat(size) {
            layout.add(diskContent)
        }
    }

    return DiskLayout(
        layout = layout,
        freeSpaceBlocks = freeSpaceBlocks,
        fileBlocks = fileBlocks,
    )
}

fun defrag(disk: DiskLayout) {
    if (disk.layout.isEmpty) {
        return
    }

    // Find the right / last file ptr.
    var rightFilePtr = findNextRightFilePtr(disk.layout.size - 1, disk.layout)
    // Find the left / first free space.
    var leftFreeSpacePtr = findNextLeftFreeSpacePtr(0, disk.layout)

    while (leftFreeSpacePtr < rightFilePtr) {
        if (leftFreeSpacePtr == -1 || rightFilePtr == -1) {
            break
        }
        disk.layout[leftFreeSpacePtr] = disk.layout[rightFilePtr]
        disk.layout[rightFilePtr] = FREE_SPACE_MARKER

        leftFreeSpacePtr = findNextLeftFreeSpacePtr(leftFreeSpacePtr, disk.layout)
        rightFilePtr = findNextRightFilePtr(rightFilePtr, disk.layout)
    }
}

fun defragBlocks(disk: DiskLayout) {
    if (disk.layout.isEmpty) {
        return
    }

    for (fileBlock in disk.fileBlocks.reversed()) {
        var freeSpaceBlockPtr = -1
        for (i in 0..<disk.freeSpaceBlocks.size) {
            val freeSpaceBlock = disk.freeSpaceBlocks[i]

            if (freeSpaceBlock.size < fileBlock.size) {
                continue
            }

            if (freeSpaceBlock.ptr >= fileBlock.ptr) {
                continue
            }

            freeSpaceBlockPtr = i
            break
        }

        if (freeSpaceBlockPtr == -1) {
            continue
        }

        val freeSpaceBlock = disk.freeSpaceBlocks[freeSpaceBlockPtr]
        val oldFileBlockPtr = fileBlock.ptr
        fileBlock.ptr = freeSpaceBlock.ptr
        for (j in 0..<fileBlock.size) {
            disk.layout[freeSpaceBlock.ptr + j] = fileBlock.id
        }

        freeSpaceBlock.size -= fileBlock.size
        freeSpaceBlock.ptr += fileBlock.size
        for (j in 0..<fileBlock.size) {
            disk.layout[oldFileBlockPtr + j] = FREE_SPACE_MARKER
        }
    }
}

fun findNextRightFilePtr(ptr: Int, diskLayout: List<Int>): Int {
    // Find the right / last file ptr.
    var rightFilePtr = ptr
    while (rightFilePtr >= 0 && diskLayout[rightFilePtr] == FREE_SPACE_MARKER) {
        rightFilePtr--
    }
    if (rightFilePtr < 0) {
        // Disk contains nothing but free space.
        return -1
    }
    check(rightFilePtr >= 0 && rightFilePtr < diskLayout.size)
    check(diskLayout[rightFilePtr] != FREE_SPACE_MARKER)

    return rightFilePtr
}

fun findNextLeftFreeSpacePtr(ptr: Int, diskLayout: List<Int>): Int {
    // Find the left / first free space.
    var leftFreeSpacePtr = ptr
    while (leftFreeSpacePtr < diskLayout.size && diskLayout[leftFreeSpacePtr] != FREE_SPACE_MARKER) {
        leftFreeSpacePtr++
    }
    if (leftFreeSpacePtr >= diskLayout.size) {
        // Disk contains nothing data.
        return -1
    }
    check(leftFreeSpacePtr >= 0 && leftFreeSpacePtr < diskLayout.size)
    check(diskLayout[leftFreeSpacePtr] == FREE_SPACE_MARKER)

    return leftFreeSpacePtr
}

fun checksum(diskLayout: List<Int>): Long {
    var checkSum = 0L
    for (i in 0..<diskLayout.size) {
        val diskContent = diskLayout[i]

        if (diskContent == FREE_SPACE_MARKER) {
            continue
        }

        checkSum += (i.toLong() * diskContent)
    }

    return checkSum
}
