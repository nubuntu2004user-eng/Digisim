package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7410(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 11,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7410,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: 1A, 1: 1B, 2: 2A, 3: 2B, 4: 2C, 5: GND, 6: 3A, 7: 3B, 8: 3C, 9: 1C, 10: VCC]
        val in1A = inputs.getOrElse(0) { Pin.UNDEFINED }
        val in1B = inputs.getOrElse(1) { Pin.UNDEFINED }
        val in2A = inputs.getOrElse(2) { Pin.UNDEFINED }
        val in2B = inputs.getOrElse(3) { Pin.UNDEFINED }
        val in2C = inputs.getOrElse(4) { Pin.UNDEFINED }
        val gnd  = inputs.getOrElse(5) { Pin.LOW }
        val in3A = inputs.getOrElse(6) { Pin.UNDEFINED }
        val in3B = inputs.getOrElse(7) { Pin.UNDEFINED }
        val in3C = inputs.getOrElse(8) { Pin.UNDEFINED }
        val in1C = inputs.getOrElse(9) { Pin.UNDEFINED }
        val vcc  = inputs.getOrElse(10) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR, Pin.ERROR, Pin.ERROR)
            return output
        }

        val out1Y = evalMultiNand(in1A, in1B, in1C)
        val out2Y = evalMultiNand(in2A, in2B, in2C)
        val out3Y = evalMultiNand(in3A, in3B, in3C)

        output = mutableListOf(out1Y, out2Y, out3Y)
        return output
    }
}
