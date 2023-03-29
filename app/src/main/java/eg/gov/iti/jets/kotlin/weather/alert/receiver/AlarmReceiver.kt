package eg.gov.iti.jets.kotlin.weather.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.text.format.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import eg.gov.iti.jets.kotlin.weather.Constants
import eg.gov.iti.jets.kotlin.weather.Constants.ALARM_ACTION
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.utils.createAlarmChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import timber.log.Timber

var mediaPlayer: MediaPlayer? = null

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        when (intent.action) {
            ALARM_ACTION -> {
                println("ALARM_ACTION")
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
                mediaPlayer?.start()
                buildNotification(context, "Alarm mode", convertDate(timeInMillis))

            }
            Constants.ACTION_SET_EXACT -> {
                println("ACTION_SET_EXACT")
                buildNotification(context, "Set Exact Time", convertDate(timeInMillis))
            }
            context.getString(R.string.dismiss) -> {
                println("Dismiss: cancel audio")

                mediaPlayer?.release()
                mediaPlayer?.stop()
                mediaPlayer = null

            }

            Constants.ACTION_SET_REPETITIVE_EXACT -> {
                println("ACTION_SET_REPETITIVE_EXACT")

                setRepetitiveAlarm(AlarmService(context))
                buildNotification(context, "Set Repetitive Exact Time", convertDate(timeInMillis))
            }


        }
    }

    private fun buildNotification(context: Context, title: String, message: String) {

        createAlarmChannel(context, title, message)
        println("Sending notification")

    }

    private fun setRepetitiveAlarm(alarmService: AlarmService) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
            Timber.d("Set alarm for next week same time - ${convertDate(this.timeInMillis)}")
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()

}