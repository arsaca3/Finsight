package com.example.finsight20.ml

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class IntentClassifier(private val context: Context) {

    private val interpreter: Interpreter
    private val wordIndex: Map<String, Int>
    private val labels: List<String>
    private val maxLen = 20

    init {
        interpreter = Interpreter(loadModelFile("intent_model.tflite"))
        wordIndex = loadTokenizer("tokenizer.json")
        labels = loadLabels("label_encoder.txt")
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels(fileName: String): List<String> {
        val reader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
        return reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun loadTokenizer(fileName: String): Map<String, Int> {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        val tokenizerObj = JSONObject(json)
        val config = tokenizerObj.getJSONObject("config")


        val wordIndexString = config.getString("word_index")
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(wordIndexString, mapType)
    }



    fun predictIntent(text: String): String {
        val sequence = textToSequence(text)
        val floatInput = Array(1) { sequence.map { it.toFloat() }.toFloatArray() }

        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(floatInput, output)

        val confidences = output[0]
        val maxIdx = confidences.indices.maxByOrNull { confidences[it] } ?: -1
        val predictedLabel = if (maxIdx != -1) labels[maxIdx] else "unknown"

        Log.d("IntentClassifier", "Input: $text | Prediction: $predictedLabel | Confidence: ${"%.2f".format(confidences[maxIdx])}")
        return predictedLabel
    }

    private fun textToSequence(text: String): FloatArray {
        val tokens = text.lowercase().split(" ")
        val sequence = tokens.map { word ->
            wordIndex[word]?.toFloat() ?: 1f
        }.take(maxLen).toMutableList()

        while (sequence.size < maxLen) {
            sequence.add(0f)
        }

        return sequence.toFloatArray()
    }
}
