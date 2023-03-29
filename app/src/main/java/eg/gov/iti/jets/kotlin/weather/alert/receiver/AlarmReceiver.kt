package eg.gov.iti.jets.kotlin.weather.alert.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import eg.gov.iti.jets.kotlin.weather.Constants
import eg.gov.iti.jets.kotlin.weather.alert.utils.createNotificationChannel
import eg.gov.iti.jets.kotlin.weather.alert.view.AlarmService
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)

        when (intent.action) {
            Constants.ACTION_SET_EXACT -> {
                buildNotification(context, "Set Exact Time", convertDate(timeInMillis))
            }

            Constants.ACTION_SET_REPETITIVE_EXACT -> {
                setRepetitiveAlarm(AlarmService(context))
                buildNotification(context, "Set Repetitive Exact Time", convertDate(timeInMillis))
            }
        }
    }

    private fun buildNotification(context: Context, title: String, message: String) {
//        Notify
//            .with(context)
//            .content {
//                this.title = title
//                text = "I got triggered at - $message"
//            }
//            .show()
//        createNotificationChannel(context).build()

        createNotificationChannel(context)

        println("hello send notification")
//        setAlarm(context)

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

//    fun setAlarm(context: Context) {
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent =
//            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, 3)
//        calendar.set(Calendar.MINUTE, 34)
//        calendar.set(Calendar.SECOND, 0)
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//    }
}