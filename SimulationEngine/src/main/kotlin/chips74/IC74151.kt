package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74151(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 14,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74151,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0..7: D0..D7, 8: A, 9: B, 10: C, 11: ~G, 12: GND, 13: VCC]
        val a = inputs.getOrElse(8) { Pin.UNDEFINED }
        val b = inputs.getOrElse(9) { Pin.UNDEFINED }
        val c = inputs.getOrElse(10) { Pin.UNDEFINED }
        val gRaw = inputs.getOrElse(11) { Pin.LOW }
        val g = if (gRaw == Pin.UNDEFINED) Pin.LOW else gRaw
        val gnd = inputs.getOrElse(12) { Pin.LOW }
        val vcc = inputs.getOrElse(13) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc) || g == Pin.ERROR) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }

        if (g == Pin.HIGH) {
            // Strobe disabled: Y = LOW, W = HIGH
            output = mutableListOf(Pin.LOW, Pin.HIGH)
            return output
        }

        if (a == Pin.ERROR || a == Pin.UNDEFINED ||
            b == Pin.ERROR || b == Pin.UNDEFINED ||
            c == Pin.ERROR || c == Pin.UNDEFINED
        ) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }

        val sel = (if (c == Pin.HIGH) 4 else 0) or (if (b == Pin.HIGH) 2 else 0) or (if (a == Pin.HIGH) 1 else 0)
        val selectedData = inputs.getOrElse(sel) { Pin.UNDEFINED }
        val outY = evalBuffer(selectedData)
        val outW = evalNot(outY)

        output = mutableListOf(outY, outW)
        return output
    }
}
