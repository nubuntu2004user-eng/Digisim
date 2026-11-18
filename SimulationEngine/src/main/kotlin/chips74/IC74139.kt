package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74139(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74139,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: ~1G, 1: 1A, 2: 1B, 3: GND, 4: ~2G, 5: 2A, 6: 2B, 7: VCC]
        val g1Raw = inputs.getOrElse(0) { Pin.LOW }
        val g1 = if (g1Raw == Pin.UNDEFINED) Pin.LOW else g1Raw
        val a1 = inputs.getOrElse(1) { Pin.UNDEFINED }
        val b1 = inputs.getOrElse(2) { Pin.UNDEFINED }
        val gnd = inputs.getOrElse(3) { Pin.LOW }
        val g2Raw = inputs.getOrElse(4) { Pin.LOW }
        val g2 = if (g2Raw == Pin.UNDEFINED) Pin.LOW else g2Raw
        val a2 = inputs.getOrElse(5) { Pin.UNDEFINED }
        val b2 = inputs.getOrElse(6) { Pin.UNDEFINED }
        val vcc = inputs.getOrElse(7) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = MutableList(8) { Pin.ERROR }
            return output
        }

        val dec1 = decode2To4(g1, a1, b1)
        val dec2 = decode2To4(g2, a2, b2)

        output = (dec1 + dec2).toMutableList()
        return output
    }

    private fun decode2To4(gActiveLow: Pin, a: Pin, b: Pin): List<Pin> {
        if (gActiveLow == Pin.ERROR) return List(4) { Pin.ERROR }
        if (gActiveLow == Pin.HIGH) return List(4) { Pin.HIGH }
        if (a == Pin.ERROR || a == Pin.UNDEFINED || b == Pin.ERROR || b == Pin.UNDEFINED) {
            return List(4) { Pin.ERROR }
        }
        val sel = (if (b == Pin.HIGH) 2 else 0) or (if (a == Pin.HIGH) 1 else 0)
        return List(4) { idx -> if (idx == sel) Pin.LOW else Pin.HIGH }
    }
}
