package com.bytebard

import com.bytebard.audio.AudioAnalyzer
import com.bytebard.audio.AudioBuilder
import com.bytebard.audio.AudioSignalBuilder

fun main() {
    val frequencies = listOf(440.0, 523.25, 659.25, 783.99)

    val builder = AudioBuilder()

    /*frequencies.forEach { frequency ->
        val signal = AudioSignalBuilder()
            .frequency(frequency)
            .duration(10.0)
            .amplitude(4.0)
            .buildSignal()
        builder.addSignal(signal)
    }*/
    val signal = AudioSignalBuilder()
        .frequency(100.0)
        .duration(1.0)
        .amplitude(4.0)
        .buildSignal()
    builder.addSignal(signal)

    builder.build()

    val signals = AudioAnalyzer.init("test.wav")
        .getSignals()
    println(signals)
}