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
import eg.gov.iti.jets.kotlin.weather.alert.utils.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import timber.log.Timber

var mediaPlayer: MediaPlayer? = null

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        val message = intent.getStringExtra(Constants.MESSAGE)
        var type = intent.getStringExtra(Constants.TYPE)
        var title = intent.getStringExtra(Constants.TITLE)
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

        when (intent.action) {
            ALARM_ACTION -> {
                println("ALARM_ACTION")
//                mediaPlayer?.prepare()
//                mediaPlayer?.start()
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

//                mediaPlayer?.prepare()
                mediaPlayer?.start()
                buildNotification(
                    context,
                    title!!,
                    "$message Alarm at ${convertDate(timeInMillis)}",
                    type!!
                )

            }
            Constants.ACTION_SET_EXACT -> {
                println("ACTION_SET_EXACT, Notification only")
                buildNotification(
                    context,
                    title!!,
                    "$message Alert at ${convertDate(timeInMillis)}",
                    type!!
                )
            }
            context.getString(R.string.dismiss) -> {
                println("Dismiss: cancel audio")

//                try {

//                    mediaPlayer?.pause()
                mediaPlayer?.stop()
                mediaPlayer?.release()
//                } catch (e: java.lang.Exception) {
//                    println("stop media player error ${e.message}")
//                }
                mediaPlayer = null


//                if (mediaPlayer?.isPlaying!!) {
//                    println("playing")
//                    mediaPlayer?.stop()
//                }

            }

            Constants.ACTION_SET_REPETITIVE_EXACT -> {
                if (title.isNullOrEmpty())
                    title = "Weather"
                if (type.isNullOrEmpty())
                    type = "alarm"
                println("ACTION_SET_REPETITIVE_EXACT")
                if (type == "alarm")
                    mediaPlayer?.start()
                setRepetitiveAlarm(
                    AlarmService(context),
                    title,
                    "Your repeated alarm\n$timeInMillis\n$message",
                    type
                )
                buildNotification(
                    context,
                    title,
                    "Your repeated alarm\n$timeInMillis\n$message",
                    type
                )

            }


        }
    }

    private fun buildNotification(context: Context, title: String, message: String, type: String) {
        println("Sending notification")

        if (type == "notification")
            createNotificationChannel(context, title, message)
        else
            createAlarmChannel(context, title, message)


    }

    private fun setRepetitiveAlarm(
        alarmService: AlarmService,
        type: String,
        message: String,
        title: String
    ) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(1)
            Timber.d("Set alarm for next day same time - ${convertDate(this.timeInMillis)}")
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis, type, message, title)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss aa", timeInMillis).toString()

}