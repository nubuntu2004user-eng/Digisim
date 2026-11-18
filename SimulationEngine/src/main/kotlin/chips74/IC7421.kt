package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7421(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 10,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7421,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: 1A, 1: 1B, 2: 1C, 3: 1D, 4: GND, 5: 2A, 6: 2B, 7: 2C, 8: 2D, 9: VCC]
        val in1A = inputs.getOrElse(0) { Pin.UNDEFINED }
        val in1B = inputs.getOrElse(1) { Pin.UNDEFINED }
        val in1C = inputs.getOrElse(2) { Pin.UNDEFINED }
        val in1D = inputs.getOrElse(3) { Pin.UNDEFINED }
        val gnd  = inputs.getOrElse(4) { Pin.LOW }
        val in2A = inputs.getOrElse(5) { Pin.UNDEFINED }
        val in2B = inputs.getOrElse(6) { Pin.UNDEFINED }
        val in2C = inputs.getOrElse(7) { Pin.UNDEFINED }
        val in2D = inputs.getOrElse(8) { Pin.UNDEFINED }
        val vcc  = inputs.getOrElse(9) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR)
            return output
        }

        val out1Y = evalMultiAnd(in1A, in1B, in1C, in1D)
        val out2Y = evalMultiAnd(in2A, in2B, in2C, in2D)

        output = mutableListOf(out1Y, out2Y)
        return output
    }
}
