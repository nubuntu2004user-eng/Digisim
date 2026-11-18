package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC7430(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 10,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_7430,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A, 1: B, 2: C, 3: D, 4: E, 5: F, 6: GND, 7: G, 8: H, 9: VCC]
        val inA = inputs.getOrElse(0) { Pin.UNDEFINED }
        val inB = inputs.getOrElse(1) { Pin.UNDEFINED }
        val inC = inputs.getOrElse(2) { Pin.UNDEFINED }
        val inD = inputs.getOrElse(3) { Pin.UNDEFINED }
        val inE = inputs.getOrElse(4) { Pin.UNDEFINED }
        val inF = inputs.getOrElse(5) { Pin.UNDEFINED }
        val gnd = inputs.getOrElse(6) { Pin.LOW }
        val inG = inputs.getOrElse(7) { Pin.UNDEFINED }
        val inH = inputs.getOrElse(8) { Pin.UNDEFINED }
        val vcc = inputs.getOrElse(9) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = mutableListOf(Pin.ERROR)
            return output
        }

        val outY = evalMultiNand(inA, inB, inC, inD, inE, inF, inG, inH)
        output = mutableListOf(outY)
        return output
    }
}
