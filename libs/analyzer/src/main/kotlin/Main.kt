package com.bytebard

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.SpectralPeakProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import com.bytebard.audio.AudioAnalyzer
import com.bytebard.audio.AudioBuilder
import com.bytebard.audio.AudioSignalBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

fun main(): Unit = runBlocking {
    val frequencies = listOf(440.0)

    val builder = AudioBuilder()
    val s1 = AudioSignalBuilder()
        .frequency(523.0)
        .duration(10.0)
        .amplitude(4.0)
        .buildSignal()
    for (freq in frequencies) {
        val signal = AudioSignalBuilder()
            .frequency(freq)
            .duration(10.0)
            .amplitude(4.0)
            .buildSignal()
        builder.addSignal(signal)
    }

    builder.build()

    /*// Set up pipes for communication between FFmpeg and TarsosDSP
    val ffmpegOut = PipedOutputStream()
    val ffmpegIn = PipedInputStream(ffmpegOut)

    // Define the FFmpeg command
    val command = CommandLine.parse("ffmpeg -ss 0.0 -i test.wav -vn -ar 44100 -ac 1 -sample_fmt s16 -f s16le pipe:1" )

    // Set up FFmpeg process
    val executor = DefaultExecutor()
    executor.streamHandler = PumpStreamHandler(ffmpegOut)

    launch(Dispatchers.IO) {
        executor.execute(command)
        ffmpegOut.close() // Close the output stream once FFmpeg is done
    }

    // Set up TarsosDSP to process the raw audio data
//    val stream = AudioSystem.getAudioInputStream(File("test.wav"))
    val audioStream = JVMAudioInputStream(AudioSystem.getAudioInputStream(ffmpegIn))

    val dispatcher = AudioDispatcher(audioStream, 1024, 0)
    val spectralPeakProcessor = SpectralPeakProcessor(1024, 0, 44100)
    dispatcher.addAudioProcessor(spectralPeakProcessor)
    dispatcher.addAudioProcessor(object : AudioProcessor {
        override fun process(audioEvent: AudioEvent): Boolean {
            val peaks = spectralPeakProcessor.frequencyEstimates
            println("Detected frequencies:")
            for (peak in peaks) {
                println("Frequency: ${peak} Hz, Amplitude: ${peak}")
            }
            return true
        }

        override fun processingFinished() {
            val peaks = spectralPeakProcessor.frequencyEstimates
            println("Detected frequencies:")
            for (peak in peaks) {
                println("Frequency: ${peak} Hz, Amplitude: ${peak}")
            }
            println("Done!")
            // Nothing to do here
        }
    })

    // Run the dispatcher in another coroutine
    async(Dispatchers.Default) {
        dispatcher.run()
    }.await()*/

    /*async(Dispatchers.Default) {
        AudioAnalyzer.init("test.wav").analyze()
    }.await()*/
}