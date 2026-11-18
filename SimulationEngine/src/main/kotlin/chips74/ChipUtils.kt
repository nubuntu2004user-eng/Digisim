package chips74

import logicGates.Pin

internal fun isChipPowered(gnd: Pin, vcc: Pin): Boolean {
    // If GND is explicitly HIGH or VCC is explicitly LOW, power is invalid
    if (gnd == Pin.HIGH || vcc == Pin.LOW || gnd == Pin.ERROR || vcc == Pin.ERROR) return false
    return true
}

internal fun evalNand(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a == Pin.HIGH && b == Pin.HIGH -> Pin.LOW
    else -> Pin.HIGH
}

internal fun evalAnd(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a == Pin.HIGH && b == Pin.HIGH -> Pin.HIGH
    else -> Pin.LOW
}

internal fun evalOr(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a == Pin.HIGH || b == Pin.HIGH -> Pin.HIGH
    else -> Pin.LOW
}

internal fun evalNor(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a == Pin.LOW && b == Pin.LOW -> Pin.HIGH
    else -> Pin.LOW
}

internal fun evalXor(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a != b -> Pin.HIGH
    else -> Pin.LOW
}

internal fun evalXnor(a: Pin, b: Pin): Pin = when {
    a == Pin.ERROR || b == Pin.ERROR -> Pin.ERROR
    a == Pin.UNDEFINED || b == Pin.UNDEFINED -> Pin.ERROR
    a == b -> Pin.HIGH
    else -> Pin.LOW
}

internal fun evalNot(a: Pin): Pin = when (a) {
    Pin.HIGH -> Pin.LOW
    Pin.LOW -> Pin.HIGH
    else -> Pin.ERROR
}

internal fun evalBuffer(a: Pin): Pin = when (a) {
    Pin.HIGH -> Pin.HIGH
    Pin.LOW -> Pin.LOW
    else -> Pin.ERROR
}

internal fun evalMultiAnd(vararg pins: Pin): Pin {
    if (pins.any { it == Pin.ERROR || it == Pin.UNDEFINED }) return Pin.ERROR
    return if (pins.all { it == Pin.HIGH }) Pin.HIGH else Pin.LOW
}

internal fun evalMultiNand(vararg pins: Pin): Pin {
    if (pins.any { it == Pin.ERROR || it == Pin.UNDEFINED }) return Pin.ERROR
    return if (pins.all { it == Pin.HIGH }) Pin.LOW else Pin.HIGH
}

internal fun evalMultiNor(vararg pins: Pin): Pin {
    if (pins.any { it == Pin.ERROR || it == Pin.UNDEFINED }) return Pin.ERROR
    return if (pins.all { it == Pin.LOW }) Pin.HIGH else Pin.LOW
}
