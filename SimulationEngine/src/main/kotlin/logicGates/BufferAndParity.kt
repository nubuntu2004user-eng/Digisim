package logicGates

class Buffer(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 1,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.BUFFER,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val a = inputs.firstOrNull() ?: Pin.UNDEFINED
        output = mutableListOf(
            when (a) {
                Pin.HIGH -> Pin.HIGH
                Pin.LOW -> Pin.LOW
                else -> Pin.ERROR
            }
        )
        return output
    }
}

class ControlledBuffer(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.CONTROLLED_BUFFER,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: DATA, 1: EN]
        val data = inputs.getOrElse(0) { Pin.UNDEFINED }
        val en = inputs.getOrElse(1) { Pin.LOW }
        val out = when (en) {
            Pin.LOW, Pin.UNDEFINED -> Pin.UNDEFINED
            Pin.HIGH -> when (data) {
                Pin.HIGH -> Pin.HIGH
                Pin.LOW -> Pin.LOW
                else -> Pin.ERROR
            }
            Pin.ERROR -> Pin.ERROR
        }
        output = mutableListOf(out)
        return output
    }
}

class ControlledInverter(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.CONTROLLED_INVERTER,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        // Inputs: [0: DATA, 1: EN]
        val data = inputs.getOrElse(0) { Pin.UNDEFINED }
        val en = inputs.getOrElse(1) { Pin.LOW }
        val out = when (en) {
            Pin.LOW, Pin.UNDEFINED -> Pin.UNDEFINED
            Pin.HIGH -> when (data) {
                Pin.HIGH -> Pin.LOW
                Pin.LOW -> Pin.HIGH
                else -> Pin.ERROR
            }
            Pin.ERROR -> Pin.ERROR
        }
        output = mutableListOf(out)
        return output
    }
}

class OddParity(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.ODD_PARITY,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val activeInputs = inputs.take(inputCount)
        if (activeInputs.size < inputCount || activeInputs.any { it == Pin.ERROR || it == Pin.UNDEFINED }) {
            output = mutableListOf(Pin.ERROR)
            return output
        }
        val highCount = activeInputs.count { it == Pin.HIGH }
        output = mutableListOf(if (highCount % 2 == 1) Pin.HIGH else Pin.LOW)
        return output
    }
}

class EvenParity(
    id: Int,
    override var inputs: MutableList<Pin>,
    override var output: MutableList<Pin>,
    override var inputCount: Int = 2,
    override val inputFrom: MutableList<inputWire>,
    override val outputTo: MutableList<outputWire>,
    override val componentType: ComponentType = ComponentType.EVEN_PARITY,
    override var highDuration: Int? = null,
    override var lowDuration: Int? = null,
    override var delayTicks: Float? = null,
) : SimulationComponent(id) {
    override suspend fun evaluate(): MutableList<Pin> {
        val activeInputs = inputs.take(inputCount)
        if (activeInputs.size < inputCount || activeInputs.any { it == Pin.ERROR || it == Pin.UNDEFINED }) {
            output = mutableListOf(Pin.ERROR)
            return output
        }
        val highCount = activeInputs.count { it == Pin.HIGH }
        output = mutableListOf(if (highCount % 2 == 0) Pin.HIGH else Pin.LOW)
        return output
    }
}
