package eg.gov.iti.jets.kotlin.weather.alert.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.alert.receiver.AlarmReceiver

class AlarmService(private val context: Context) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setExactAlarm(timeInMillis: Long, type: String, message: String, title: String, id: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = if (type == "notification") {
                        putExtra(Constants.TYPE, "notification")
                        Constants.ACTION_SET_EXACT
                    } else {
                        putExtra(Constants.TYPE, "alarm")
                        Constants.ALARM_ACTION
                    }
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra("fff", "a")
                    putExtra(Constants.MESSAGE, message)
                    putExtra(Constants.Alarm_ID, id)
                    putExtra(Constants.TITLE, title)
                }, id
            )
        )
    }


    fun stopAlarm(timeInMillis: Long, type: String, message: String, title: String, id: Long) {
        alarmManager.cancel(
            getCancelledPendingIntent(
                getIntent().apply {
                    action = if (type == "notification") {
                        putExtra(Constants.TYPE, "notification")
                        Constants.ACTION_SET_EXACT
                    } else {
                        putExtra(Constants.TYPE, "alarm")
                        Constants.ALARM_ACTION
                    }
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra(Constants.MESSAGE, message)
                    putExtra(Constants.Alarm_ID, id)
                    putExtra(Constants.TITLE, title)
                },
                id
            )
        )

    }

    private fun getPendingIntent(intent: Intent, id: Long) =

        PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getCancelledPendingIntent(intent: Intent, id: Long) =
        PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT

        )


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }


    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

}