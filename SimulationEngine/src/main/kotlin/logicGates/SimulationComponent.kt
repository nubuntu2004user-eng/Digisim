package logicGates

import kotlinx.serialization.Serializable

@Serializable
abstract class SimulationComponent(val id : Int) {
    abstract var inputs: MutableList<Pin>
    abstract var output: MutableList<Pin>

    abstract var inputCount : Int

    abstract val inputFrom : MutableList<inputWire>

    abstract val outputTo : MutableList<outputWire>

    abstract val componentType : ComponentType

    //componentSpecial things
    abstract var delayTicks: Float?
    abstract var highDuration : Int?
    abstract var lowDuration : Int?
    open var statefulClockPin: Pin = Pin.LOW
    open var statefulMemory: MutableList<Int> = mutableListOf()
    abstract suspend fun evaluate(): MutableList<Pin>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimulationComponent) return false
        if (this::class != other::class) return false
        return id == other.id && inputs == other.inputs && output == other.output
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + inputs.hashCode()
        result = 31 * result + output.hashCode()
        return result
    }
}

enum class Pin { HIGH, LOW , UNDEFINED , ERROR}

enum class ComponentType {
    NAND, AND, OR, NOR, XOR, XNOR, NOT,
    BUFFER, CONTROLLED_BUFFER, CONTROLLED_INVERTER, ODD_PARITY, EVEN_PARITY,
    INPUT, OUTPUT, CLOCK, BUTTON,
    CONSTANT_HIGH, CONSTANT_LOW, PROBE, TUNNEL,
    MULTIPLEXER_2_TO_1, MULTIPLEXER_4_TO_1, DEMULTIPLEXER_1_TO_2, DEMULTIPLEXER_1_TO_4,
    DECODER_2_TO_4, DECODER_3_TO_8, PRIORITY_ENCODER_4_TO_2,
    HALF_ADDER, FULL_ADDER, ADDER_4BIT, SUBTRACTOR_4BIT, COMPARATOR_4BIT, NEGATOR_4BIT,
    RS_FLIP_FLOP, JK_FLIP_FLOP, D_FLIP_FLOP, T_FLIP_FLOP,
    JK_FLIP_FLOP_ASYNC, SR_FLIP_FLOP_ASYNC, D_FLIP_FLOP_ASYNC, T_FLIP_FLOP_ASYNC,
    REGISTER_4BIT, COUNTER_4BIT, SHIFT_REGISTER_4BIT, RAM_16X4, ROM_16X4,
    LED, SEVEN_SEGMENT_DISPLAY, HEX_DIGIT_DISPLAY, DIP_SWITCH_4BIT,
    CUSTOM,
    IC_7400, IC_7402, IC_7404, IC_7407, IC_7408,
    IC_7410, IC_7411, IC_7420, IC_7421, IC_7427, IC_7430,
    IC_7432, IC_7473, IC_7474, IC_7476, IC_7486,
    IC_74138, IC_74139, IC_74151, IC_74157, IC_74266, IC_74283
}

data class inputWire(
    val sourceGateId : Int,
    val portId : Int,
    var value : Pin = Pin.UNDEFINED
)

data class outputWire(
    val targetGateId : Int,
    val portId : Int,
    var value: Pin = Pin.UNDEFINED,
    val sourcePortId: Int = 0
)