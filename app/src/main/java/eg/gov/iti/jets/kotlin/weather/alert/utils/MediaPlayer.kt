package eg.gov.iti.jets.kotlin.weather.alert.utils

import android.content.Context
import eg.gov.iti.jets.kotlin.weather.R
import android.media.MediaPlayer

class MediaPlayer {

    companion object {
        @Volatile
        private var INSTANCE: MediaPlayer? = null
        fun getInstance(context: Context): MediaPlayer {
            return INSTANCE ?: synchronized(this) {
                val i = MediaPlayer.create(context, R.raw.alarm)
                println("kkkkkkkk")
                INSTANCE = i
                i
            }
        }
    }
}