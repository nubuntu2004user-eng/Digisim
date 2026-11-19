package com.example.digisim.DrawingLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.ComponentType
import com.example.digisim.ParsingLogic.Wire
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.roundToInt

private const val STEP = 10
private const val CLEARANCE = 8f
private const val LEAD_DISTANCE = 10f

private data class GridPt(val gx: Int, val gy: Int)

private data class SearchState(
    val gx: Int,
    val gy: Int,
    val dir: Int, // 0=Right, 1=Down, 2=Left, 3=Up, 4=Start
    val gCost: Int,
    val fCost: Int
) : Comparable<SearchState> {
    override fun compareTo(other: SearchState): Int = this.fCost.compareTo(other.fCost)
}

fun resolveSourcePortPosition(component: Component, portIndex: Int): Offset {
    return if (component.componentType == ComponentType.INPUT ||
        component.componentType == ComponentType.CLOCK ||
        component.componentType == ComponentType.BUTTON ||
        component.componentType == ComponentType.CONSTANT_HIGH ||
        component.componentType == ComponentType.CONSTANT_LOW
    ) {
        component.rotatedFindPortOffset()
    } else {
        component.rotatedOutputPortPositions().getOrElse(portIndex) { component.rotatedFindPortOffset() }
    }
}

fun resolveTargetPortPosition(component: Component, portIndex: Int): Offset {
    return if (component.componentType == ComponentType.OUTPUT ||
        component.componentType == ComponentType.PROBE ||
        component.componentType == ComponentType.LED
    ) {
        component.rotatedFindPortOffset()
    } else {
        component.rotatedInputPortPositions().getOrElse(portIndex) { component.rotatedFindPortOffset() }
    }
}

private fun portOutwardDirection(component: Component, portPos: Offset): Offset {
    val b = component.rotatedBounds()
    val dLeft = abs(portPos.x - b.left)
    val dRight = abs(portPos.x - b.right)
    val dTop = abs(portPos.y - b.top)
    val dBottom = abs(portPos.y - b.bottom)
    val minD = minOf(dLeft, dRight, dTop, dBottom)
    return when (minD) {
        dLeft -> Offset(-1f, 0f)
        dRight -> Offset(1f, 0f)
        dTop -> Offset(0f, -1f)
        else -> Offset(0f, 1f)
    }
}

private fun snapToGrid(value: Float): Int =
    (value / STEP.toFloat()).roundToInt()

private fun gridToWorld(g: Int): Float =
    (g * STEP).toFloat()

/**
 * Routes all wires orthogonally around components and prevents collinear/parallel wire overlap
 * (allowing only perpendicular 90-degree crossings).
 */
fun routeAllWires(
    components: List<Component>,
    wires: List<Wire>
): Map<Int, List<Offset>> {
    if (wires.isEmpty()) return emptyMap()

    // 1. Build padded component obstacle bounds
    val compMap = components.associateBy { it.ID }
    val obstacles = components.map { comp ->
        val b = comp.rotatedBounds()
        comp.ID to Rect(
            left = b.left - CLEARANCE,
            top = b.top - CLEARANCE,
            right = b.right + CLEARANCE,
            bottom = b.bottom + CLEARANCE
        )
    }

    // Track occupied grid edges and vertex directions:
    // Horizontal unit edge between (gx, gy) and (gx + 1, gy) -> key(minGx, gy)
    // Vertical unit edge between (gx, gy) and (gx, gy + 1) -> key(gx, minGy)
    val usedHorizontalEdges = HashSet<Long>()
    val usedVerticalEdges = HashSet<Long>()
    val usedHorizontalNodes = HashSet<Long>()
    val usedVerticalNodes = HashSet<Long>()

    fun pack(x: Int, y: Int): Long =
        (x.toLong() shl 32) or (y.toLong() and 0xFFFFFFFFL)

    fun isCellBlockedByComponent(gx: Int, gy: Int, ignoreStart: GridPt, ignoreGoal: GridPt): Boolean {
        if ((gx == ignoreStart.gx && gy == ignoreStart.gy) ||
            (gx == ignoreGoal.gx && gy == ignoreGoal.gy)
        ) {
            return false
        }
        val wx = gridToWorld(gx)
        val wy = gridToWorld(gy)
        for ((_, rect) in obstacles) {
            // Strictly inside padded obstacle rect
            if (wx > rect.left && wx < rect.right && wy > rect.top && wy < rect.bottom) {
                return true
            }
        }
        return false
    }

    fun isSegmentBlockedByComponent(gx1: Int, gy1: Int, gx2: Int, gy2: Int, ignoreStart: GridPt, ignoreGoal: GridPt): Boolean {
        if (isCellBlockedByComponent(gx2, gy2, ignoreStart, ignoreGoal)) return true
        val midX = (gridToWorld(gx1) + gridToWorld(gx2)) * 0.5f
        val midY = (gridToWorld(gy1) + gridToWorld(gy2)) * 0.5f
        for ((_, rect) in obstacles) {
            if (midX > rect.left && midX < rect.right && midY > rect.top && midY < rect.bottom) {
                return true
            }
        }
        return false
    }

    fun computeClearGridPoint(component: Component, portPos: Offset): GridPt {
        val dir = portOutwardDirection(component, portPos)
        val b = component.rotatedBounds()
        val targetX = when {
            dir.x < 0f -> b.left - LEAD_DISTANCE
            dir.x > 0f -> b.right + LEAD_DISTANCE
            else -> portPos.x
        }
        val targetY = when {
            dir.y < 0f -> b.top - LEAD_DISTANCE
            dir.y > 0f -> b.bottom + LEAD_DISTANCE
            else -> portPos.y
        }
        var gx = snapToGrid(targetX)
        var gy = snapToGrid(targetY)

        // Ensure the lead point is outside all component obstacles; step outward up to 6 times if needed
        val stepDx = dir.x.roundToInt()
        val stepDy = dir.y.roundToInt()
        var attempts = 0
        val dummy = GridPt(Int.MIN_VALUE, Int.MIN_VALUE)
        while (attempts < 6 && isCellBlockedByComponent(gx, gy, dummy, dummy)) {
            gx += stepDx
            gy += stepDy
            attempts++
        }
        return GridPt(gx, gy)
    }

    val result = LinkedHashMap<Int, List<Offset>>()

    for (wire in wires) {
        val sourceComp = compMap[wire.sourceGateId] ?: continue
        val targetComp = compMap[wire.targetGateId] ?: continue

        val sourcePort = resolveSourcePortPosition(sourceComp, wire.sourcePortIndex)
        val targetPort = resolveTargetPortPosition(targetComp, wire.targetPortIndex)

        val startGrid = computeClearGridPoint(sourceComp, sourcePort)
        val goalGrid = computeClearGridPoint(targetComp, targetPort)

        val gridPath = findOrthogonalGridPath(
            start = startGrid,
            goal = goalGrid,
            components = components,
            usedHorizontalEdges = usedHorizontalEdges,
            usedVerticalEdges = usedVerticalEdges,
            usedHorizontalNodes = usedHorizontalNodes,
            usedVerticalNodes = usedVerticalNodes,
            isSegmentBlocked = { x1, y1, x2, y2 ->
                isSegmentBlockedByComponent(x1, y1, x2, y2, startGrid, goalGrid)
            },
            pack = ::pack
        )

        // Record occupied edges and node orientations for this wire so subsequent wires do not overlap it
        for (i in 0 until gridPath.size - 1) {
            val a = gridPath[i]
            val b = gridPath[i + 1]
            if (a.gy == b.gy && a.gx != b.gx) {
                val minX = minOf(a.gx, b.gx)
                val maxX = maxOf(a.gx, b.gx)
                for (x in minX until maxX) {
                    usedHorizontalEdges.add(pack(x, a.gy))
                }
                for (x in minX..maxX) {
                    usedHorizontalNodes.add(pack(x, a.gy))
                }
            } else if (a.gx == b.gx && a.gy != b.gy) {
                val minY = minOf(a.gy, b.gy)
                val maxY = maxOf(a.gy, b.gy)
                for (y in minY until maxY) {
                    usedVerticalEdges.add(pack(a.gx, y))
                }
                for (y in minY..maxY) {
                    usedVerticalNodes.add(pack(a.gx, y))
                }
            }
        }

        // Convert grid points to world waypoints with orthogonal port connections
        val worldPoints = buildOrthogonalPolyline(
            sourcePort = sourcePort,
            sourceDir = portOutwardDirection(sourceComp, sourcePort),
            targetPort = targetPort,
            targetDir = portOutwardDirection(targetComp, targetPort),
            gridPath = gridPath
        )

        result[wire.id] = simplifyCollinearPoints(worldPoints)
    }

    return result
}

private fun findOrthogonalGridPath(
    start: GridPt,
    goal: GridPt,
    components: List<Component>,
    usedHorizontalEdges: Set<Long>,
    usedVerticalEdges: Set<Long>,
    usedHorizontalNodes: Set<Long>,
    usedVerticalNodes: Set<Long>,
    isSegmentBlocked: (Int, Int, Int, Int) -> Boolean,
    pack: (Int, Int) -> Long
): List<GridPt> {
    if (start == goal) return listOf(start)

    // Determine bounding search box with generous padding around components and endpoints
    var minGx = minOf(start.gx, goal.gx)
    var maxGx = maxOf(start.gx, goal.gx)
    var minGy = minOf(start.gy, goal.gy)
    var maxGy = maxOf(start.gy, goal.gy)

    for (c in components) {
        val b = c.rotatedBounds()
        minGx = minOf(minGx, snapToGrid(b.left) - 4)
        maxGx = maxOf(maxGx, snapToGrid(b.right) + 4)
        minGy = minOf(minGy, snapToGrid(b.top) - 4)
        maxGy = maxOf(maxGy, snapToGrid(b.bottom) + 4)
    }
    minGx -= 12
    maxGx += 12
    minGy -= 12
    maxGy += 12

    val dxs = intArrayOf(1, 0, -1, 0)
    val dys = intArrayOf(0, 1, 0, -1)

    fun stateKey(gx: Int, gy: Int, dir: Int): Long {
        val xy = ((gx.toLong() and 0x1FFFFFL) shl 21) or (gy.toLong() and 0x1FFFFFL)
        return (xy shl 4) or (dir.toLong() and 0xFL)
    }

    fun heuristic(gx: Int, gy: Int): Int =
        (abs(gx - goal.gx) + abs(gy - goal.gy)) * 10

    // First try strict non-overlapping search; if no path exists, allow a fallback pass
    for (strictOverlap in booleanArrayOf(true, false)) {
        val open = PriorityQueue<SearchState>()
        val bestG = HashMap<Long, Int>()
        val parent = HashMap<Long, Long>()

        val startKey = stateKey(start.gx, start.gy, 4)
        open.add(SearchState(start.gx, start.gy, 4, 0, heuristic(start.gx, start.gy)))
        bestG[startKey] = 0

        var expansions = 0
        val maxExpansions = 25000

        while (open.isNotEmpty() && expansions < maxExpansions) {
            val cur = open.poll()
            val curKey = stateKey(cur.gx, cur.gy, cur.dir)
            val recordedG = bestG[curKey] ?: continue
            if (cur.gCost > recordedG) continue

            if (cur.gx == goal.gx && cur.gy == goal.gy) {
                // Reconstruct path
                val path = ArrayList<GridPt>()
                var k: Long? = curKey
                while (k != null) {
                    val xy = k ushr 4
                    var rawX = ((xy ushr 21) and 0x1FFFFFL).toInt()
                    var rawY = (xy and 0x1FFFFFL).toInt()
                    if ((rawX and 0x100000) != 0) rawX = rawX or -0x200000
                    if ((rawY and 0x100000) != 0) rawY = rawY or -0x200000
                    val pt = GridPt(rawX, rawY)
                    if (path.isEmpty() || path.last() != pt) {
                        path.add(pt)
                    }
                    k = parent[k]
                }
                path.reverse()
                return path
            }

            expansions++

            for (dir in 0..3) {
                // Disallow immediate 180-degree reversal
                if (cur.dir < 4 && (cur.dir + 2) % 4 == dir) continue

                val nx = cur.gx + dxs[dir]
                val ny = cur.gy + dys[dir]

                if (nx < minGx || nx > maxGx || ny < minGy || ny > maxGy) continue

                if (isSegmentBlocked(cur.gx, cur.gy, nx, ny)) continue

                val isHoriz = (dir == 0 || dir == 2)
                val edgeKey = if (isHoriz) {
                    pack(minOf(cur.gx, nx), cur.gy)
                } else {
                    pack(cur.gx, minOf(cur.gy, ny))
                }
                val destNodeKey = pack(nx, ny)
                val isGoalNode = (nx == goal.gx && ny == goal.gy)

                var overlapPenalty = 0
                if (isHoriz) {
                    val collinearEdge = edgeKey in usedHorizontalEdges
                    val collinearNode = !isGoalNode && (destNodeKey in usedHorizontalNodes)
                    if (collinearEdge || collinearNode) {
                        if (strictOverlap) continue
                        overlapPenalty += 500
                    }
                    // Perpendicular 90-degree crossing is allowed with a small crossing cost
                    if (destNodeKey in usedVerticalNodes) {
                        overlapPenalty += 15
                    }
                } else {
                    val collinearEdge = edgeKey in usedVerticalEdges
                    val collinearNode = !isGoalNode && (destNodeKey in usedVerticalNodes)
                    if (collinearEdge || collinearNode) {
                        if (strictOverlap) continue
                        overlapPenalty += 500
                    }
                    // Perpendicular 90-degree crossing is allowed with a small crossing cost
                    if (destNodeKey in usedHorizontalNodes) {
                        overlapPenalty += 15
                    }
                }

                val bendCost = if (cur.dir != 4 && cur.dir != dir) 25 else 0
                val stepCost = 10 + bendCost + overlapPenalty
                val tentativeG = cur.gCost + stepCost

                val nextKey = stateKey(nx, ny, dir)
                val prevBest = bestG[nextKey]
                if (prevBest == null || tentativeG < prevBest) {
                    bestG[nextKey] = tentativeG
                    parent[nextKey] = curKey
                    open.add(SearchState(nx, ny, dir, tentativeG, tentativeG + heuristic(nx, ny)))
                }
            }
        }
    }

    // Fallback simple orthogonal L/Z path if search limit exceeded
    val midGx = (start.gx + goal.gx) / 2
    return listOf(
        start,
        GridPt(midGx, start.gy),
        GridPt(midGx, goal.gy),
        goal
    )
}

private fun buildOrthogonalPolyline(
    sourcePort: Offset,
    sourceDir: Offset,
    targetPort: Offset,
    targetDir: Offset,
    gridPath: List<GridPt>
): List<Offset> {
    val pts = ArrayList<Offset>(gridPath.size + 4)
    pts.add(sourcePort)

    if (gridPath.isNotEmpty()) {
        val firstWorld = Offset(gridToWorld(gridPath.first().gx), gridToWorld(gridPath.first().gy))
        if (sourcePort.x != firstWorld.x && sourcePort.y != firstWorld.y) {
            if (abs(sourceDir.x) > 0f) {
                pts.add(Offset(firstWorld.x, sourcePort.y))
            } else {
                pts.add(Offset(sourcePort.x, firstWorld.y))
            }
        }
        for (gp in gridPath) {
            pts.add(Offset(gridToWorld(gp.gx), gridToWorld(gp.gy)))
        }
        val lastWorld = Offset(gridToWorld(gridPath.last().gx), gridToWorld(gridPath.last().gy))
        if (lastWorld.x != targetPort.x && lastWorld.y != targetPort.y) {
            if (abs(targetDir.x) > 0f) {
                pts.add(Offset(lastWorld.x, targetPort.y))
            } else {
                pts.add(Offset(targetPort.x, lastWorld.y))
            }
        }
    }

    pts.add(targetPort)
    return pts
}

private fun simplifyCollinearPoints(points: List<Offset>): List<Offset> {
    if (points.size <= 2) return points
    val deduped = ArrayList<Offset>(points.size)
    for (p in points) {
        if (deduped.isEmpty() || (abs(deduped.last().x - p.x) > 0.1f || abs(deduped.last().y - p.y) > 0.1f)) {
            deduped.add(p)
        }
    }
    if (deduped.size <= 2) return deduped

    val simplified = ArrayList<Offset>(deduped.size)
    simplified.add(deduped.first())
    for (i in 1 until deduped.size - 1) {
        val prev = simplified.last()
        val curr = deduped[i]
        val next = deduped[i + 1]
        val sameX = abs(prev.x - curr.x) < 0.1f && abs(curr.x - next.x) < 0.1f
        val sameY = abs(prev.y - curr.y) < 0.1f && abs(curr.y - next.y) < 0.1f
        if (!sameX && !sameY) {
            simplified.add(curr)
        }
    }
    simplified.add(deduped.last())
    return simplified
}
