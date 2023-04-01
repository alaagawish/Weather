package eg.gov.iti.jets.kotlin.weather.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.text.format.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.ALARM_ACTION
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.utils.createAlarmChannel
import eg.gov.iti.jets.kotlin.weather.alert.utils.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import eg.gov.iti.jets.kotlin.weather.editor
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION
import timber.log.Timber

lateinit var mediaPlayer: MediaPlayer

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        var message = intent.getStringExtra(Constants.MESSAGE)
        var type = intent.getStringExtra(Constants.TYPE)
        var title = intent.getStringExtra(Constants.TITLE)
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

        if (sharedPreferences.getString(NOTIFICATION, null) == "enable") {
            when (intent.action) {
                ALARM_ACTION -> {
                    println("ALARM_ACTION")
                    println(
                        "ALARM_ACTION  ${
                            sharedPreferences.getBoolean(
                                "isNotFirstTime",
                                false
                            )
                        }"
                    )
//                mediaPlayer?.prepare()
//                mediaPlayer?.start()
//                mediaPlayer?.stop()
//                mediaPlayer?.reset()
//                mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

//                mediaPlayer?.prepare()
//                mediaPlayer.start()
                    if (title.isNullOrEmpty())
                        title = "Weather"
                    if (type.isNullOrEmpty())
                        type = "Alarm is stopped"
                    if (message.isNullOrEmpty())
                        message = ""

                    buildNotification(
                        context,
                        title,
                        "$message Alarm at ${convertDate(timeInMillis)}",
                        type
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
                    println("ggggggggggggggggggggggggggggggggggggggg")
//                if (sharedPreferences.getBoolean("isFirstTime", true)) {
//                    editor.putBoolean("isFirstTime", false)
//                    editor.commit()
//
//                    MediaPlayer.create(context, R.raw.alarm)
//
//                } else {
//                    editor.putBoolean("isFirstTime",true)
//                    editor.commit()
//                    println("Dismiss: cancel audio")
//                    mediaPlayer.stop()
//                    mediaPlayer.release()
//
//                }


//                try {

//                    mediaPlayer?.pause()

//                } catch (e: java.lang.Exception) {
//                    println("stop media player error ${e.message}")
//                }
//                mediaPlayer = null


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
    }

    private fun buildNotification(context: Context, title: String, message: String, type: String) {
        println("Sending notification")

        if (type == "notification")
            createNotificationChannel(context, title, message)
        else {
            if (!sharedPreferences.getBoolean("isNotFirstTime", false)) {
                editor.putBoolean("isNotFirstTime", true)
                editor.commit()

                mediaPlayer.start()

            } else {
                editor.putBoolean("isNotFirstTime", false)
                editor.commit()
                println("Dismiss: cancel audio")
                mediaPlayer.stop()
                mediaPlayer.release()

            }
            createAlarmChannel(context, title, message)
        }


    }

    private fun setRepetitiveAlarm(
        alarmService: AlarmService,
        type: String,
        message: String,
        title: String
    ) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.SECONDS.toMillis(60)
            Timber.d("Set alarm for next 30 seconds same time - ${convertDate(this.timeInMillis)}")
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis, type, message, title)
    }

    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss aa", timeInMillis).toString()

}