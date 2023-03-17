package eg.gov.iti.jets.kotlin.weather.alert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlertViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is alert Fragment"
    }
    val text: LiveData<String> = _text
}