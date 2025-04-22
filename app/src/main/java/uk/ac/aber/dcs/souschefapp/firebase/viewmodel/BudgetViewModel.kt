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
        val processedBitmap = preprocessBitmap(bitmap)
        _receiptBitmap.value = processedBitmap
        recognizeTextFromImage(processedBitmap)
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val filteredText = visionText.textBlocks
                    .filter { it.boundingBox?.width() ?: 0 > 50 } // ignore tiny text
                    .joinToString("\n") { it.text }

                _ocrText.value = filteredText
            }
            .addOnFailureListener { e ->
                _ocrText.value = "Error: ${e.localizedMessage}"
            }
    }

    private fun preprocessBitmap(original: Bitmap): Bitmap {
        val targetWidth = 1080
        val targetHeight = (original.height.toFloat() / original.width * targetWidth).toInt()
        val resized = Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true)

        val grayBitmap = Bitmap.createBitmap(resized.width, resized.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayBitmap)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) }) // grayscale
        }

        canvas.drawBitmap(resized, 0f, 0f, paint)
        return grayBitmap
    }
}