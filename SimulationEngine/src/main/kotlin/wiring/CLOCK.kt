package wiring

import kotlinx.coroutines.delay
import logicGates.BasicComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire
import kotlin.time.Duration.Companion.milliseconds

class CLOCK(
    id : Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 0,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType : ComponentType = ComponentType.CLOCK,
    override var delay: Int? = null,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null
    ): BasicComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        if (delay !== null){
        delay(delay?.milliseconds?: 999.milliseconds)
        }
        output = if (output[0] == Pin.LOW) mutableListOf(Pin.HIGH) else mutableListOf(Pin.LOW)
        return output.toMutableList()
        }


    }
