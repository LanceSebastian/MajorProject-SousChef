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

        // Grayscale
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(resized, 0f, 0f, paint)

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
        finalCanvas.drawBitmap(grayscale, 0f, 0f, finalPaint)

        return finalOutput
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val filteredText = visionText.textBlocks
                    .flatMap { it.lines }
                    .filter { it.text.length > 3 && (it.boundingBox?.width() ?: 0) > 50 }
                    .joinToString("\n") { it.text }

                _ocrText.value = filteredText.ifBlank { "No readable text found." }
            }
            .addOnFailureListener { e ->
                _ocrText.value = "Error: ${e.localizedMessage}"
            }
    }
}