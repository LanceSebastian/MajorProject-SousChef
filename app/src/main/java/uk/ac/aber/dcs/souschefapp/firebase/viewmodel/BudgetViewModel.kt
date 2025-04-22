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

class BudgetViewModel : ViewModel() {

    private val _receiptBitmap = MutableLiveData<Bitmap?>()
    val receiptBitmap: LiveData<Bitmap?> = _receiptBitmap

    private val _ocrText = MutableLiveData<String>()
    val ocrText: LiveData<String> = _ocrText

    fun setBitmap(bitmap: Bitmap) {
        _receiptBitmap.value = bitmap
        recognizeTextFromImage(bitmap)
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _ocrText.value = visionText.text
            }
            .addOnFailureListener { e ->
                _ocrText.value = "Error: ${e.localizedMessage}"
            }
    }
}