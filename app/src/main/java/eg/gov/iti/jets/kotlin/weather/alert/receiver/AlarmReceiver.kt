package eg.gov.iti.jets.kotlin.weather.alert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import eg.gov.iti.jets.kotlin.weather.alert.utils.createAlarmChannel
import eg.gov.iti.jets.kotlin.weather.alert.utils.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.ALARM_ACTION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION
import eg.gov.iti.jets.kotlin.weather.utils.ConvertTime
import java.util.*
import java.util.concurrent.TimeUnit
import eg.gov.iti.jets.kotlin.weather.alert.utils.MediaPlayer as MedPlayer

lateinit var mediaPlayer: MediaPlayer

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        var message = intent.getStringExtra(Constants.MESSAGE)
        var type = intent.getStringExtra(Constants.TYPE)
        var title = intent.getStringExtra(Constants.TITLE)
        var id = intent.getStringExtra(Constants.Alarm_ID)?.toLong()
        mediaPlayer = MedPlayer.getInstance(context)


        if (sharedPreferences!!.getString(NOTIFICATION, null) == "enable") {
            when (intent.action) {
                ALARM_ACTION -> {
                    println("ALARM_ACTION")
                    if (title.isNullOrEmpty())
                        title = "Weather"
                    if (type.isNullOrEmpty())
                        type = "Alarm is stopped"
                    if (message.isNullOrEmpty())
                        message = ""
                    if (id == null)
                        id = 0L

                    buildNotification(
                        context,
                        title,
                        "$message Alarm at ${
                            ConvertTime.getDateFormat(
                                "dd/MM/yyyy hh:mm:ss aa",
                                timeInMillis
                            )
                        }",
                        type,
                        id
                    )

                }

                Constants.ACTION_SET_EXACT -> {

                    println("ACTION_SET_EXACT, Notification only")
                    if (title.isNullOrEmpty())
                        title = "Weather"
                    if (type.isNullOrEmpty())
                        type = "Alarm is stopped"
                    if (message.isNullOrEmpty())
                        message = ""
                    if (id == null)
                        id = 0L

                    buildNotification(
                        context,
                        title!!,
                        "$message Alert at ${
                            ConvertTime.getDateFormat(
                                "dd/MM/yyyy hh:mm:ss aa",
                                timeInMillis
                            )
                        }",
                        type!!,
                        id!!
                    )
                }

                Constants.ACTION_SET_REPETITIVE_EXACT -> {
                    if (title.isNullOrEmpty())
                        title = "Weather"
                    if (type.isNullOrEmpty())
                        type = "alarm"
                    if (id == null)
                        id = 0
                    println("ACTION_SET_REPETITIVE_EXACT")
                    if (type == "alarm")
                        mediaPlayer.pause()
                    setRepetitiveAlarm(
                        AlarmService(context),
                        title,
                        "Your repeated alarm\n$timeInMillis\n$message",
                        type, id
                    )
                    buildNotification(
                        context,
                        title,
                        "Your repeated alarm\n$timeInMillis\n$message",
                        type, id
                    )

                }


            }
        }
    }

    private fun buildNotification(
        context: Context,
        title: String,
        message: String,
        type: String,
        id: Long
    ) {
        if (type == "notification")
            createNotificationChannel(context, title, message)
        else {
            if (title == "Weather") {
                println("Dismiss: cancel audio")
                mediaPlayer.stop()

            } else {
                mediaPlayer.start()
                createAlarmChannel(context, title, message, id)

            }
        }


    }

    private fun setRepetitiveAlarm(
        alarmService: AlarmService,
        type: String,
        message: String,
        title: String, id: Long
    ) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.SECONDS.toMillis(60)
        }
        alarmService.setExactAlarm(cal.timeInMillis, type, message, title, id)
    }


}