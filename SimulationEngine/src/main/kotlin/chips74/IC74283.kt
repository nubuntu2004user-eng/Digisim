package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74283(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 11,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74283,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A1, 1: B1, 2: A2, 3: B2, 4: A3, 5: B3, 6: A4, 7: B4, 8: C0, 9: GND, 10: VCC]
        // Outputs: [0: S1, 1: S2, 2: S3, 3: S4, 4: C4]
        val a1 = inputs.getOrElse(0) { Pin.UNDEFINED }
        val b1 = inputs.getOrElse(1) { Pin.UNDEFINED }
        val a2 = inputs.getOrElse(2) { Pin.UNDEFINED }
        val b2 = inputs.getOrElse(3) { Pin.UNDEFINED }
        val a3 = inputs.getOrElse(4) { Pin.UNDEFINED }
        val b3 = inputs.getOrElse(5) { Pin.UNDEFINED }
        val a4 = inputs.getOrElse(6) { Pin.UNDEFINED }
        val b4 = inputs.getOrElse(7) { Pin.UNDEFINED }
        val c0Raw = inputs.getOrElse(8) { Pin.LOW }
        val c0 = if (c0Raw == Pin.UNDEFINED) Pin.LOW else c0Raw
        val gnd = inputs.getOrElse(9) { Pin.LOW }
        val vcc = inputs.getOrElse(10) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = MutableList(5) { Pin.ERROR }
            return output
        }

        val operandPins = listOf(a1, b1, a2, b2, a3, b3, a4, b4, c0)
        if (operandPins.any { it == Pin.ERROR || it == Pin.UNDEFINED }) {
            output = MutableList(5) { Pin.ERROR }
            return output
        }

        val valA = (if (a1 == Pin.HIGH) 1 else 0) or
            (if (a2 == Pin.HIGH) 2 else 0) or
            (if (a3 == Pin.HIGH) 4 else 0) or
            (if (a4 == Pin.HIGH) 8 else 0)

        val valB = (if (b1 == Pin.HIGH) 1 else 0) or
            (if (b2 == Pin.HIGH) 2 else 0) or
            (if (b3 == Pin.HIGH) 4 else 0) or
            (if (b4 == Pin.HIGH) 8 else 0)

        val carryIn = if (c0 == Pin.HIGH) 1 else 0
        val sum = valA + valB + carryIn

        output = mutableListOf(
            if ((sum and 1) != 0) Pin.HIGH else Pin.LOW,  // S1
            if ((sum and 2) != 0) Pin.HIGH else Pin.LOW,  // S2
            if ((sum and 4) != 0) Pin.HIGH else Pin.LOW,  // S3
            if ((sum and 8) != 0) Pin.HIGH else Pin.LOW,  // S4
            if ((sum and 16) != 0) Pin.HIGH else Pin.LOW  // C4
        )
        return output
    }
}
