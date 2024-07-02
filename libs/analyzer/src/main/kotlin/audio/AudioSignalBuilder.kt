package com.bytebard.audio

import com.bytebard.models.AudioSignal
import kotlin.math.PI

class AudioSignalBuilder(
    private val constructSignal: ((AudioSignal) -> AudioBuilder?)? = null
) {
    private var duration: Double = 0.0
    private var frequency: Double = 0.0
    private var amplitude: Double = 0.0
    private var phase: Double = 0.0

    fun duration(duration: Double): AudioSignalBuilder {
        this.duration = duration
        return this
    }

    fun frequency(frequency: Double): AudioSignalBuilder {
        this.frequency = frequency
        return this
    }

    fun amplitude(amplitude: Double): AudioSignalBuilder {
        this.amplitude = amplitude
        return this
    }

    fun phase(phase: Double): AudioSignalBuilder {
        this.phase = phase
        return this
    }

    fun addToAudioBuilder(): AudioBuilder {
        val phaseShift = 2 * PI * frequency * phase
        val signal = AudioSignal(amplitude, frequency, duration, phase, phaseShift)
        return constructSignal?.invoke(signal) ?: throw IllegalStateException("No audio builder provided")
    }

    fun buildSignal(): AudioSignal {
        val phaseShift = 2 * PI * frequency * phase
        val signal = AudioSignal(amplitude, frequency, duration, phase, phaseShift)
        return signal
    }
}