package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateViewModel: ViewModel() {

    private val _dateDialogState = MutableLiveData(false)
    val dateDialogState: LiveData<Boolean> = _dateDialogState

    private val _datePicked = MutableLiveData(LocalDate.now())
    val datePicked: LiveData<LocalDate> = _datePicked

    val datePickedEpoch = MediatorLiveData<Long>().apply {
        addSource(_datePicked) { date ->
            value = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    val monthFormat = MediatorLiveData<String>().apply {
        addSource(_datePicked) { date ->
            value = DateTimeFormatter.ofPattern("MMMM yyyy").format(date)
        }
    }

    val dayFormat = MediatorLiveData<String>().apply {
        addSource(_datePicked) { date ->
            value = DateTimeFormatter.ofPattern("EEEE dd").format(date)
        }
    }

    fun onDatePicked(newDate: LocalDate) {
        _datePicked.value = newDate
    }

    fun setDialogState(show: Boolean) {
        _dateDialogState.value = show
    }

    fun shiftDatePickedBy(days: Long) {
        _datePicked.value = _datePicked.value?.plusDays(days)
    }

    private fun readLogFromDate(dateMillis: Long) {
        // Your log-fetching logic here
    }

    init {
        datePickedEpoch.observeForever { epoch ->
            readLogFromDate(epoch)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Optional: clean up observer if needed
    }
}