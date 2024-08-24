package com.bytebard.audio

import com.bytebard.models.AudioSignal
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.abs
import kotlin.math.sin

class AudioBuilder {

    private var sampleRate: Int = 44100
    private val signals = mutableListOf<AudioSignal>()
    private val sampleSizeInBits = 16
    private val channels = 1

    fun constructSignal(): AudioSignalBuilder {
        val constructSignal :(AudioSignal) -> AudioBuilder = { audioSignal ->
            signals.add(audioSignal)
            this
        }
        return AudioSignalBuilder(constructSignal)
    }

    fun addSignal(signal: AudioSignal): AudioBuilder {
        signals.add(signal)
        return this
    }

    fun build() {
        val maxDuration = signals.maxOf { it.duration }
        val numSamples = (maxDuration * sampleRate).toInt()
        val samples = DoubleArray(numSamples)

        for (signal in signals) {
            val signalSamples = generateSineWave(signal)
            val delay = (signal.phase * sampleRate).toInt()
            for (i in signalSamples.indices) {
                if (i + signal.phase < samples.size) {
                    samples[i + delay] += signalSamples[i]
                }
            }
        }

        // Normalize the combined signal to prevent clipping
        val maxSample = samples.maxOf { abs(it) }
        if (maxSample > 1.0) {
            for (i in samples.indices) {
                samples[i] /= maxSample
            }
        }

        val bytes = samplesToByteArray(samples)
        writeToFile(bytes)
    }

    private fun generateSineWave(signal: AudioSignal): DoubleArray {
        val numSamples = (signal.duration * sampleRate).toInt()
        val samples = DoubleArray(numSamples)

        for (i in samples.indices) {
            samples[i] = signal.amplitude * sin(2.0 * Math.PI * signal.frequency * i / sampleRate + signal.phase)
        }

        return samples
    }

    private fun samplesToByteArray(samples: DoubleArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(samples.size * 2) // 16-bit PCM encoding

        for (sample in samples) {
            val intSample = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            byteBuffer.putShort(intSample.toShort())
        }

        return byteBuffer.array()
    }

    fun sampleRate(sampleRate: Int): AudioBuilder {
        this.sampleRate = sampleRate
        return this
    }

    private fun writeToFile(byteArray: ByteArray) {
        val format = AudioFormat(sampleRate.toFloat(), sampleSizeInBits, channels, true, true)
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val audioInputStream = AudioInputStream(byteArrayInputStream, format, byteArray.size.toLong() / format.frameSize)

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File("audio.wav"))
    }
}