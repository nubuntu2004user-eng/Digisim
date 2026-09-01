package wiring

import engineLogic.ClockManager
import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import kotlin.math.roundToInt

class CLOCK(
    id : Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType : ComponentType = ComponentType.CLOCK,
    override var delayTicks: Float? = null,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    val clockManager: ClockManager
    ): BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        if (delayTicks != null) {
            val roundedDelayTicks = delayTicks!!.roundToInt().coerceAtLeast(1)

            if ((clockManager.tick % roundedDelayTicks) == 0) {
                output = if (output[0] == Pin.LOW) mutableListOf(Pin.HIGH) else mutableListOf(Pin.LOW)
            }
        } else {
            output = if (output[0] == Pin.LOW) mutableListOf(Pin.HIGH) else mutableListOf(Pin.LOW)
        }

        return output.toMutableList()
    }


    }
