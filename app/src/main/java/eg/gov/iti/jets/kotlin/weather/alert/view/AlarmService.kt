package eg.gov.iti.jets.kotlin.weather.alert.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import eg.gov.iti.jets.kotlin.weather.Constants
import eg.gov.iti.jets.kotlin.weather.RandomUtil
import eg.gov.iti.jets.kotlin.weather.alert.receiver.AlarmReceiver

class AlarmService(private val context: Context) {
    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long, type: String, message: String, title: String) {
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
                    putExtra(Constants.MESSAGE, message)
                    putExtra(Constants.TITLE, title)
                }
            )
        )
    }

    //1 Week
    fun setRepetitiveAlarm(timeInMillis: Long, type: String, message: String, title: String) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = if (type == "notification") {
                        putExtra(Constants.TYPE, "notification")
                        Constants.ACTION_SET_REPETITIVE_EXACT
                    } else {
                        putExtra(Constants.TYPE, "alarm")
                        Constants.ACTION_SET_REPETITIVE_EXACT
                    }
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra(Constants.MESSAGE, message)
                    putExtra(Constants.TITLE, title)
                }
            )
        )
    }

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            getRandomRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
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

    private fun getRandomRequestCode() = RandomUtil.getRandomInt()

}