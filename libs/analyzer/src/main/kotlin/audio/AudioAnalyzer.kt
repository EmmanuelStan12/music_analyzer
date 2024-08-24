package com.bytebard.audio

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.SpectralPeakProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import be.tarsos.dsp.util.fft.FFT
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log10


class AudioAnalyzer private constructor(
    private val file: File,
    private val audioInputStream: AudioInputStream,
) {
    companion object {

        const val SAMPLE_RATE = 44100
        const val BUFFER_SIZE = 1024
        const val BUFFER_OVERLAP = 0

        fun init(path: String): AudioAnalyzer {
            val file = File(path)
            val audioInputStream = AudioSystem.getAudioInputStream(file)
            return AudioAnalyzer(file, audioInputStream)
        }
    }

    suspend fun analyze() = suspendCoroutine<Any> { continuation ->
        val audioStream = JVMAudioInputStream(audioInputStream)

        // create a new dispatcher
//        val dispatcher = AudioDispatcher(audioStream, BUFFER_SIZE, BUFFER_OVERLAP)
        val dispatcher = AudioDispatcherFactory.fromPipe(file.path, SAMPLE_RATE, BUFFER_SIZE, BUFFER_OVERLAP)


        // add a processor
        /*val spectralPeakProcessor = SpectralPeakProcessor(BUFFER_SIZE, BUFFER_OVERLAP, SAMPLE_RATE)
        dispatcher.addAudioProcessor(spectralPeakProcessor)*/
        /*dispatcher.addAudioProcessor(PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, audioStream.format.sampleRate, BUFFER_SIZE) { result, event ->
            if (result.pitch != -1f) {
                val timeStamp: Double = event.timeStamp
                val pitch: Float = result.pitch
                val probability: Float = result.probability
                val rms: Double = event.rms * 100
                val message = String.format(
                    "Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n",
                    timeStamp,
                    pitch,
                    probability,
                    rms
                )
                println(message)
            }
        })*/

/*        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun process(event: AudioEvent?): Boolean {
                return true
            }

            override fun processingFinished() {
                continuation.resume(Unit)
            }
        })*/
        val fft = FFT(BUFFER_SIZE)
        val amplitudeSize = fft.size() / 2
        val amplitudes = FloatArray(amplitudeSize)
//        val powerSpectrum = PowerSpectrum(fft)

        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun process(audioEvent: AudioEvent?): Boolean {
                val floatBuffer = audioEvent?.floatBuffer ?: return true
                fft.forwardTransform(floatBuffer)
                fft.modulus(floatBuffer, amplitudes)

                // Calculate frequency for each bin
                val sampleRate = audioEvent.sampleRate
                val binSize = sampleRate / fft.size()
                for (i in amplitudes.indices) {
                    val frequency = i * binSize
                    val amplitude = amplitudes[i]
                    val db = 20 * log10(amplitude.toDouble())
                    println("Frequency: $frequency Hz, Amplitude: $db dB")
                }
                return true
            }

            override fun processingFinished() {
                println("Processing finished")
            }
        })

        dispatcher.run()
    }

}