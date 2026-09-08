package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7404(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7404,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        val in1A = inputs.getOrElse(0) { Pin.UNDEFINED }
        val in2A = inputs.getOrElse(1) { Pin.UNDEFINED }
        val in3A = inputs.getOrElse(2) { Pin.UNDEFINED }
        val gnd  = inputs.getOrElse(3) { Pin.LOW }
        val in4A = inputs.getOrElse(4) { Pin.UNDEFINED }
        val in5A = inputs.getOrElse(5) { Pin.UNDEFINED }
        val in6A = inputs.getOrElse(6) { Pin.UNDEFINED }
        val vcc  = inputs.getOrElse(7) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }

        val out1Y = evalNot(in1A)
        val out2Y = evalNot(in2A)
        val out3Y = evalNot(in3A)
        val out4Y = evalNot(in4A)
        val out5Y = evalNot(in5A)
        val out6Y = evalNot(in6A)

        output = mutableListOf(out1Y, out2Y, out3Y, out4Y, out5Y, out6Y)
        return output
    }
}
