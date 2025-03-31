package uk.ac.aber.dcs.souschefapp.room_viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository

class InstructionViewModel (
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)


}