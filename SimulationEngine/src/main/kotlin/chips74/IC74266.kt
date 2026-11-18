package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74266(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 10,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74266,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: GND, 5: 3A, 6: 3B, 7: 4A, 8: 4B, 9: VCC]
        val in1A = inputs.getOrElse(0) { Pin.UNDEFINED }
        val in1B = inputs.getOrElse(1) { Pin.UNDEFINED }
        val in2A = inputs.getOrElse(2) { Pin.UNDEFINED }
        val in2B = inputs.getOrElse(3) { Pin.UNDEFINED }
        val gnd  = inputs.getOrElse(4) { Pin.LOW }
        val in3A = inputs.getOrElse(5) { Pin.UNDEFINED }
        val in3B = inputs.getOrElse(6) { Pin.UNDEFINED }
        val in4A = inputs.getOrElse(7) { Pin.UNDEFINED }
        val in4B = inputs.getOrElse(8) { Pin.UNDEFINED }
        val vcc  = inputs.getOrElse(9) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }

        val out1Y = evalXnor(in1A, in1B)
        val out2Y = evalXnor(in2A, in2B)
        val out3Y = evalXnor(in3A, in3B)
        val out4Y = evalXnor(in4A, in4B)

        output = mutableListOf(out1Y, out2Y, out3Y, out4Y)
        return output
    }
}
