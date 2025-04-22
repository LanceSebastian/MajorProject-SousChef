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

    // Preprocess the bitmap (resize, grayscale, adjust contrast/brightness)
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        val width = 1080
        val scale = width.toFloat() / bitmap.width
        val height = (bitmap.height * scale).toInt()

        // Resize
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // Grayscale
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(resized, 0f, 0f, paint)

        // Apply binarization after grayscale conversion
        val binarized = binarizeImage(grayscale) // Apply binarization

        // Contrast & Brightness
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

        val finalOutput = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val finalCanvas = Canvas(finalOutput)
        val finalPaint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(contrastMatrix)
        }
        finalCanvas.drawBitmap(binarized, 0f, 0f, finalPaint)

        return finalOutput
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

    private fun binarizeImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val threshold = 127 // This is a fixed threshold, you can adjust it dynamically if needed

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get pixel color
                val pixelColor = bitmap.getPixel(x, y)

                // Convert color to grayscale using the RGB values
                val red = Color.red(pixelColor)
                val green = Color.green(pixelColor)
                val blue = Color.blue(pixelColor)
                val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt() // Grayscale formula

                // Apply the threshold
                val newColor = if (gray < threshold) Color.BLACK else Color.WHITE

                // Set the pixel to either black or white based on the threshold
                result.setPixel(x, y, newColor)
            }
        }

        return result
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