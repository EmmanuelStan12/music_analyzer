package com.bytebard.audio

import com.bytebard.models.AudioSignal
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
import java.io.File
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException
import kotlin.experimental.and
import kotlin.math.*

class AudioAnalyzer private constructor(
    private val audioInputStream: AudioInputStream
) {
    private val sampleRate = 44100.0

    companion object {

        fun init(path: String): AudioAnalyzer {
            val file = File(path)
            val audioInputStream = AudioSystem.getAudioInputStream(file)
            return AudioAnalyzer(audioInputStream)
        }
    }

    private fun audioSamples(audioInputStream: AudioInputStream): DoubleArray {
        /*
        * Audio Format: The format variable holds the audio format information (e.g., sample rate, number of channels, etc.).
        * */
        val format = audioInputStream.format
        // Check if the format is supported
        if (!format.encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            throw UnsupportedAudioFileException("Unsupported audio format: " + format.encoding);
        }
        /*
        * Bytes per Frame: bytesPerFrame holds the number of bytes for each frame of audio data.
        * A frame can contain multiple samples, especially in stereo audio (where each frame contains one sample for each channel).
        * */
        val bytesPerFrame = format.frameSize
        /*
        * Number of Bytes: numBytes calculates the total number of bytes in the audio file by multiplying the number of frames by the bytes per frame.
        * */
        val numBytes = audioInputStream.frameLength * bytesPerFrame
        /*
        * Samples Array: An array samples is created to hold the audio samples, with the length determined by the number of bytes read divided by the bytes per frame.
        * */

        // Allocate buffer for reading audio data
        val buffer = ByteArray(bytesPerFrame * 1024)
        val samples = mutableListOf<Double>()

        var numBytesRead: Int = audioInputStream.read(buffer)
        val sampleSizeInBits = format.sampleSizeInBits
        while (numBytesRead > 0) {
            var i = 0;
            while (i < numBytesRead) {
                when (sampleSizeInBits) {
                    8 -> {

                        /** Normalize the 8-bit integer
                        * The & 0xFF mask is used to ensure the value is interpreted correctly as an unsigned 8-bit value.
                        * This is often necessary in languages like Java, where the byte type is signed, meaning its range is from -128 to 127.
                        * However, when dealing with raw binary data, we often need to treat these values as unsigned (0 to 255).
                        * Then divides by 128f to put the range from [0, 2] and then -1f to reduce it to [0, 1]
                        * */
                        val byte = (buffer[i].toInt() and 0xFF).toFloat()
                        samples.add((byte / 128f - 1f).toDouble())
                    }
                    16 -> {
                        // Convert to unsigned integer
                        val byte = ((buffer[i].toInt() and 0xFF) shl 8) or (buffer[i + 1].toInt() and 0xFF)
                        val reversed = byte.toShort().reverseBytes()
                        val sample = reversed.toFloat() / Short.MAX_VALUE.toFloat()
                        samples.add(sample.toDouble())
                    }
                }
                i += bytesPerFrame
            }
            numBytesRead = audioInputStream.read(buffer)
        }

        /*val audioBytes = ByteArray(numBytes.toInt())
        val bytesRead = audioInputStream.read(audioBytes)

        val samples = DoubleArray(bytesRead / bytesPerFrame)
        for (i in samples.indices) {
            val index = i * bytesPerFrame
            samples[i] = ByteBuffer.wrap(audioBytes, index, 2).short.toDouble()
        }*/

        return DoubleArray(samples.size) { i -> samples[i] }
    }

    private fun analyzeAudio(samples: DoubleArray): List<AudioSignal> {
        val n = samples.size
        val fft = DoubleFFT_1D(n)
        /*
        * The fftData array is created with a length of 2 * n.
        * This is because the FFT routine used (realForwardFull) expects an array of complex numbers (real and imaginary parts).
        * Since a real number can be represented with two parts (real and imaginary), we need twice the number of elements to store these parts.
        * */
        val fftData = DoubleArray(n * 2)
        /*
        * This loop is where the actual audio samples are copied into the fftData array. Here's what happens:
        * samples[i] represents the amplitude value of the i-th sample.
        * fftData[2 * i] is the position where the real part of the i-th complex number is stored.
        * Since the imaginary part of the samples is zero (because we're working with real numbers),
        * we don't explicitly set fftData[2 * i + 1] to zero; it defaults to zero.
        * */
        for (i in samples.indices) {
            // [re, im, re, im]
            fftData[2 * i] = samples[i]
        }
        fft.realForward(fftData)

        val signals = hashMapOf<Double, AudioSignal>()
        for (i in 0 until n / 2) {
            // Real content is in the even parts
            val re = fftData[2 * i]

            // Imaginary content is at the odd parts.
            val im = fftData[2 * i + 1]

            // The amplitude is sqrt(re^2 + im^2)
            val amplitude = hypot(re, im)

            if (amplitude < 2) {
                continue
            }

            val phaseShift = atan2(im, re)

            // freq = n * Fs/N
            val frequency = i * sampleRate / n

            val phase = 2 * PI * frequency / phaseShift

            signals[amplitude] = AudioSignal(amplitude, frequency,n / sampleRate, phase, phaseShift)
        }
        return signals.values.toList()
    }

    fun getSignals(): List<AudioSignal> {
        val samples = audioSamples(audioInputStream)
        return analyzeAudio(samples)
    }

    private fun Short.reverseBytes(): Short {
        // short is 2 bytes in the jvm, it doesn't use os specifications.
        // so if a short is like: 10011001 11011101
        // The switching the two bytes should reverse the short
        val result = ((((this.toInt()) and 0xFF) shl 8) or ((this.toInt() ushr 8) and 0xFF)).toShort()
        return result
    }
}