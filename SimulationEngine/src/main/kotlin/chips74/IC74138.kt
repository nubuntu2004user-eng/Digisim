package chips74

import logicGates.SimulationComponent
import logicGates.ComponentType
import logicGates.Pin
import logicGates.inputWire
import logicGates.outputWire

class IC74138(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 8,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.IC_74138,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {

    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: A, 1: B, 2: C, 3: ~G2A, 4: ~G2B, 5: G1, 6: GND, 7: VCC]
        val a = inputs.getOrElse(0) { Pin.UNDEFINED }
        val b = inputs.getOrElse(1) { Pin.UNDEFINED }
        val c = inputs.getOrElse(2) { Pin.UNDEFINED }
        val g2aRaw = inputs.getOrElse(3) { Pin.LOW }
        val g2a = if (g2aRaw == Pin.UNDEFINED) Pin.LOW else g2aRaw
        val g2bRaw = inputs.getOrElse(4) { Pin.LOW }
        val g2b = if (g2bRaw == Pin.UNDEFINED) Pin.LOW else g2bRaw
        val g1Raw = inputs.getOrElse(5) { Pin.HIGH }
        val g1 = if (g1Raw == Pin.UNDEFINED) Pin.HIGH else g1Raw
        val gnd = inputs.getOrElse(6) { Pin.LOW }
        val vcc = inputs.getOrElse(7) { Pin.HIGH }

        if (!isChipPowered(gnd, vcc)) {
            output = MutableList(8) { Pin.ERROR }
            return output
        }

        if (g1 == Pin.ERROR || g2a == Pin.ERROR || g2b == Pin.ERROR) {
            output = MutableList(8) { Pin.ERROR }
            return output
        }

        val enabled = g1 == Pin.HIGH && g2a == Pin.LOW && g2b == Pin.LOW
        if (!enabled) {
            output = MutableList(8) { Pin.HIGH }
            return output
        }

        if (a == Pin.ERROR || a == Pin.UNDEFINED ||
            b == Pin.ERROR || b == Pin.UNDEFINED ||
            c == Pin.ERROR || c == Pin.UNDEFINED
        ) {
            output = MutableList(8) { Pin.ERROR }
            return output
        }

        val bitA = if (a == Pin.HIGH) 1 else 0
        val bitB = if (b == Pin.HIGH) 2 else 0
        val bitC = if (c == Pin.HIGH) 4 else 0
        val selectedIndex = bitC or bitB or bitA

        output = MutableList(8) { idx -> if (idx == selectedIndex) Pin.LOW else Pin.HIGH }
        return output
    }
}
