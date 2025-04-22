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
import android.graphics.Color
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

    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        val width = 1080
        val scale = width.toFloat() / bitmap.width
        val height = (bitmap.height * scale).toInt()

        // Resize
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // Convert to grayscale
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(resized, 0f, 0f, paint)

        // Apply dynamic thresholding
        val thresholded = dynamicThreshold(grayscale)

        return thresholded
    }

    // Dynamic thresholding (adaptive thresholding)
    private fun dynamicThreshold(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                // Get pixel color
                val pixel = bitmap.getPixel(x, y)
                // Convert to grayscale intensity (using the luminance formula)
                val gray = (Color.red(pixel) * 0.2989 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()

                // Calculate adaptive thresholding (mean value in a window)
                val threshold = calculateLocalThreshold(bitmap, x, y, 15)  // Using a 15x15 window for local threshold

                // Apply the threshold to the pixel
                val newPixel = if (gray > threshold) Color.WHITE else Color.BLACK
                outputBitmap.setPixel(x, y, newPixel)
            }
        }

        return outputBitmap
    }

    // Function to calculate the local threshold using mean intensity in a window around the pixel
    private fun calculateLocalThreshold(bitmap: Bitmap, x: Int, y: Int, windowSize: Int): Int {
        val halfWindow = windowSize / 2
        var sum = 0
        var count = 0

        for (dy in -halfWindow..halfWindow) {
            for (dx in -halfWindow..halfWindow) {
                val nx = x + dx
                val ny = y + dy
                if (nx >= 0 && nx < bitmap.width && ny >= 0 && ny < bitmap.height) {
                    val pixel = bitmap.getPixel(nx, ny)
                    val gray = (Color.red(pixel) * 0.2989 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
                    sum += gray
                    count++
                }
            }
        }

        return if (count > 0) sum / count else 0
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
            // Exclude small blocks of text
            if (block.text.length < 4) continue

            extractedText.append("Block: ${block.text}\n")

            // Process lines within the block
            for (line in block.lines) {
                // Exclude very short lines
                if (line.text.length < 4) continue

                extractedText.append("Line: ${line.text}\n")

                // Process words in the line
                for (element in line.elements) {
                    // Exclude small words
                    if (element.text.length < 4) continue

                    extractedText.append("Word: ${element.text}\n")
                }
            }
        }

        _ocrText.value = extractedText.toString()
    }
}