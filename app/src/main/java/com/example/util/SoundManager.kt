package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    var isSoundEnabled: Boolean = true
    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playClick() {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
            } catch (_: Exception) {}
        }
    }

    fun playScanBeep() {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 80)
            } catch (_: Exception) {}
        }
    }

    fun playUnlockFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                playArpeggio(listOf(523.25, 659.25, 783.99, 1046.50), 90)
            } catch (_: Exception) {}
        }
    }

    fun playWaterSquirt() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(800.0, 1600.0, 120)
        }
    }

    fun playLaunch() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(300.0, 900.0, 100)
        }
    }

    fun playSmash() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(600.0, 120.0, 150)
        }
    }

    fun playFlip() {
        if (!isSoundEnabled) return
        scope.launch {
            playArpeggio(listOf(440.0, 554.37, 659.25), 60)
        }
    }

    fun playBoomerangWhoosh() {
        if (!isSoundEnabled) return
        scope.launch {
            playChirp(400.0, 750.0, 180)
        }
    }

    fun playDotVoice(phraseIndex: Int) {
        if (!isSoundEnabled) return
        scope.launch {
            val melody = when (phraseIndex % 4) {
                0 -> listOf(523.25, 659.25, 783.99, 659.25, 1046.50) // Cheerful hello!
                1 -> listOf(880.0, 783.99, 880.0, 1046.50) // Giggle
                2 -> listOf(659.25, 783.99, 987.77, 1174.66) // Adventure!
                else -> listOf(587.33, 739.99, 880.0, 1174.66) // High five!
            }
            playArpeggio(melody, 85)
        }
    }

    private fun playChirp(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            var currentFreq = startFreq
            val freqStep = (endFreq - startFreq) / numSamples
            var phase = 0.0

            for (i in 0 until numSamples) {
                currentFreq += freqStep
                phase += 2.0 * Math.PI * currentFreq / sampleRate
                val envelope = 1.0 - (i.toDouble() / numSamples)
                samples[i] = (sin(phase) * 32767.0 * envelope * 0.5).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, numSamples)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun playArpeggio(notes: List<Double>, noteDurationMs: Int) {
        try {
            val sampleRate = 22050
            val totalSamplesPerNote = (sampleRate * (noteDurationMs / 1000.0)).toInt()
            val totalSamples = totalSamplesPerNote * notes.size
            val samples = ShortArray(totalSamples)

            var offset = 0
            for (freq in notes) {
                var phase = 0.0
                for (i in 0 until totalSamplesPerNote) {
                    phase += 2.0 * Math.PI * freq / sampleRate
                    val envelope = 1.0 - (i.toDouble() / totalSamplesPerNote) * 0.2
                    samples[offset + i] = (sin(phase) * 32767.0 * envelope * 0.5).toInt().toShort()
                }
                offset += totalSamplesPerNote
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(totalSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, totalSamples)
            audioTrack.play()
            Thread.sleep((noteDurationMs * notes.size).toLong() + 30)
            audioTrack.release()
        } catch (_: Exception) {}
    }
}
