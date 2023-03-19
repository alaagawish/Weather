package eg.gov.iti.jets.kotlin.weather.network

import eg.gov.iti.jets.kotlin.weather.model.OneCall

sealed class APIState {
    class Success(val oneCall: OneCall) : APIState()
    class Failure(val e: Throwable) : APIState()
    object Waiting : APIState()

}