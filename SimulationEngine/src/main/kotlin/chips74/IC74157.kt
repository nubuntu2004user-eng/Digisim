package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74157(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 12,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74157,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: SEL, 1: 1A, 2: 1B, 3: 2A, 4: 2B, 5: GND, 6: 3A, 7: 3B, 8: 4A, 9: 4B, 10: ~G, 11: VCC]
        val sel = inputs.getOrElse(0) { Pin.UNDEFINED }
        val in1A = inputs.getOrElse(1) { Pin.UNDEFINED }
        val in1B = inputs.getOrElse(2) { Pin.UNDEFINED }
        val in2A = inputs.getOrElse(3) { Pin.UNDEFINED }
        val in2B = inputs.getOrElse(4) { Pin.UNDEFINED }
        val gnd = inputs.getOrElse(5) { Pin.LOW }
        val in3A = inputs.getOrElse(6) { Pin.UNDEFINED }
        val in3B = inputs.getOrElse(7) { Pin.UNDEFINED }
        val in4A = inputs.getOrElse(8) { Pin.UNDEFINED }
        val in4B = inputs.getOrElse(9) { Pin.UNDEFINED }
        val gRaw = inputs.getOrElse(10) { Pin.LOW }
        val g = if (gRaw == Pin.UNDEFINED) Pin.LOW else gRaw
        val vcc = inputs.getOrElse(11) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc) || g == Pin.ERROR) {
            output = MutableList(4) { Pin.ERROR }
            return output
        }

        if (g == Pin.HIGH) {
            // Strobe HIGH forces all outputs LOW
            output = MutableList(4) { Pin.LOW }
            return output
        }

        if (sel == Pin.ERROR || sel == Pin.UNDEFINED) {
            output = MutableList(4) { Pin.ERROR }
            return output
        }

        val pickB = sel == Pin.HIGH
        output = mutableListOf(
            evalBuffer(if (pickB) in1B else in1A),
            evalBuffer(if (pickB) in2B else in2A),
            evalBuffer(if (pickB) in3B else in3A),
            evalBuffer(if (pickB) in4B else in4A)
        )
        return output
    }
}
