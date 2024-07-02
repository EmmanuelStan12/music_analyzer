package com.bytebard.models

data class AudioSignal(
    val amplitude: Double,
    val frequency: Double,
    val duration: Double,
    val phase: Double,
    val phaseShift: Double,
)
