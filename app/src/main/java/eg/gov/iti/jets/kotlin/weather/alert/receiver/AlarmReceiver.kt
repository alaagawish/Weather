package eg.gov.iti.jets.kotlin.weather.alert.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.utils.createAlarmChannel
import eg.gov.iti.jets.kotlin.weather.alert.utils.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import eg.gov.iti.jets.kotlin.weather.sharedPreferences
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.ALARM_ACTION
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION
import eg.gov.iti.jets.kotlin.weather.utils.ConvertTime
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import eg.gov.iti.jets.kotlin.weather.alert.utils.MediaPlayer as MedPlayer

lateinit var mediaPlayer: MediaPlayer

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        var message = intent.getStringExtra(Constants.MESSAGE)
        var type = intent.getStringExtra(Constants.TYPE)
        var type1 = intent.getStringExtra("fff")
        var title = intent.getStringExtra(Constants.TITLE)
        var id = intent.getStringExtra(Constants.Alarm_ID)?.toInt()
        mediaPlayer = MedPlayer.getInstance(context)

        if (type1 == "a") {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(getPendingIntent(context))
        }
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

                    buildNotification(
                        context,
                        title,
                        "$message Alarm at ${
                            ConvertTime.getDateFormat(
                                "dd/MM/yyyy hh:mm:ss aa",
                                timeInMillis
                            )
                        }",
                        type
                    )

                }
                Constants.ACTION_SET_EXACT -> {
                    println("ACTION_SET_EXACT, Notification only")
                    buildNotification(
                        context,
                        title!!,
                        "$message Alert at ${
                            ConvertTime.getDateFormat(
                                "dd/MM/yyyy hh:mm:ss aa",
                                timeInMillis
                            )
                        }",
                        type!!
                    )
                }
                context.getString(R.string.dismiss) -> {

                }

                Constants.ACTION_SET_REPETITIVE_EXACT -> {
                    if (title.isNullOrEmpty())
                        title = "Weather"
                    if (type.isNullOrEmpty())
                        type = "alarm"
                    if (id != null)
                        id = 0
                    println("ACTION_SET_REPETITIVE_EXACT")
                    if (type == "alarm")
                        mediaPlayer?.start()
                    setRepetitiveAlarm(
                        AlarmService(context),
                        title,
                        "Your repeated alarm\n$timeInMillis\n$message",
                        type, id!!
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
            if (title == "Weather") {
                println("Dismiss: cancel audio")
                mediaPlayer.pause()

            } else {
                mediaPlayer.start()
            }
            createAlarmChannel(context, title, message)
        }


    }

    private fun setRepetitiveAlarm(
        alarmService: AlarmService,
        type: String,
        message: String,
        title: String, id: Int
    ) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.SECONDS.toMillis(60)
            Timber.d(
                "Set alarm for next 30 seconds same time - ${
                    ConvertTime.getDateFormat(
                        "dd/MM/yyyy hh:mm:ss aa",
                        this.timeInMillis
                    )
                }"
            )
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis, type, message, title, id)
    }

    private fun getPendingIntent(ctxt: Context): PendingIntent {
        val i = Intent(ctxt, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(ctxt, 0, i, 0)
    }

}