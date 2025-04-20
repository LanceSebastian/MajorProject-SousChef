package uk.ac.aber.dcs.souschefapp.firebase

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val url: String) : UploadState()
    data class Error(val message: String) : UploadState()
}