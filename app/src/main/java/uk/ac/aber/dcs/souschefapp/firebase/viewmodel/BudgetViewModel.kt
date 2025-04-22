package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.google.mlkit.vision.text.Text

class BudgetViewModel : ViewModel() {
    private val _receiptBitmap = MutableLiveData<Bitmap?>()
    val receiptBitmap: LiveData<Bitmap?> = _receiptBitmap

    private val _ocrText = MutableLiveData<String>()
    val ocrText: LiveData<String> = _ocrText

    fun setBitmap(bitmap: Bitmap) {
        _receiptBitmap.value = bitmap
        val processed = preprocessBitmap(bitmap)
        recognizeTextFromImage(processed)
    }

    // Preprocess the bitmap (resize, grayscale, adjust contrast/brightness, sharpen)
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        val width = 1080
        val scale = width.toFloat() / bitmap.width
        val height = (bitmap.height * scale).toInt()

        // Resize the bitmap to make the image smaller
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // Convert to grayscale
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // Corrected: no nullable config
        val canvas = Canvas(grayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(resized, 0f, 0f, paint)

        // Apply sharpening
        val sharpened = sharpenImage(grayscale)

        // Contrast & Brightness adjustment
        val contrast = 1.5f
        val brightness = -30f
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val finalOutput = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // Corrected: no nullable config
        val finalCanvas = Canvas(finalOutput)
        val finalPaint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(contrastMatrix)
        }
        finalCanvas.drawBitmap(sharpened, 0f, 0f, finalPaint)

        return finalOutput
    }

    // Sharpen image function
    private fun sharpenImage(bitmap: Bitmap): Bitmap {
        // Create a color matrix for sharpening the image
        val sharpenMatrix = floatArrayOf(
            0f, -1f, 0f, 0f, 0f,
            -1f, 5f, -1f, 0f, 0f,
            0f, -1f, 0f, 0f, 0f
        )

        val matrix = ColorMatrix(sharpenMatrix)  // Initialize the ColorMatrix with the sharpen matrix

        // Create a new bitmap with non-null config
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        // Create a canvas and apply the sharpened effect
        val canvas = Canvas(outputBitmap)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)  // Apply the sharpened ColorMatrix via a ColorMatrixColorFilter
        }

        // Draw the bitmap on the canvas with the sharpen effect
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return outputBitmap
    }

    // Recognize text from image
    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                parseBlocks(visionText)
            }
            .addOnFailureListener { e ->
                _ocrText.value = "Error: ${e.localizedMessage}"
            }
    }

    // Parse blocks from OCR results
    private fun parseBlocks(visionText: Text) {
        val extractedText = StringBuilder()

        // Iterate over text blocks
        for (block in visionText.textBlocks) {
            // Exclude blocks that are too small or consist of non-informative characters
            if (block.text.length < 4 || containsNonAlphanumeric(block.text)) continue

            extractedText.append("Block: ${block.text}\n")

            // Process lines within the block
            for (line in block.lines) {
                // Exclude short lines or lines with only numbers or symbols
                if (line.text.length < 3 || containsNonAlphanumeric(line.text)) continue

                extractedText.append("Line: ${line.text}\n")

                // Process words in the line
                for (element in line.elements) {
                    // Exclude short words or words with just symbols/numbers
                    if (element.text.length < 3 || containsNonAlphanumeric(element.text)) continue

                    extractedText.append("Word: ${element.text}\n")
                }
            }
        }

        _ocrText.value = extractedText.toString().ifBlank { "No readable text found." }
    }

    // Helper function to check if the text contains non-alphanumeric characters
    private fun containsNonAlphanumeric(text: String): Boolean {
        return text.any { !it.isLetterOrDigit() && it != ' ' }
    }
}
