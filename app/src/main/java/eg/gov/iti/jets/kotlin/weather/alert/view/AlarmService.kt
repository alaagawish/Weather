package eg.gov.iti.jets.kotlin.weather.alert.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.RandomUtil
import eg.gov.iti.jets.kotlin.weather.alert.receiver.AlarmReceiver
import eg.gov.iti.jets.kotlin.weather.editor

class AlarmService(private val context: Context) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    fun setExactAlarm(timeInMillis: Long, type: String, message: String, title: String, id: Int) {
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

    //1 Week
    fun setRepetitiveAlarm(
        timeInMillis: Long,
        type: String,
        message: String,
        title: String,
        id: Int
    ) {
        val pendingIntent = getPendingIntent(
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
                editor.putBoolean("isNotFirstTime", false)
                editor.commit()
            }, id
        )
        println("aaaa $pendingIntent")
        setAlarm(
            timeInMillis,
            pendingIntent
        )
    }

    fun stopAlarm(timeInMillis: Long, type: String, message: String, title: String, id: Int) {
        println("deletingggg")
        alarmManager.cancel(
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
                    putExtra(Constants.Alarm_ID, id)
                    putExtra(Constants.TITLE, title)
                }, 1
            )
        )

    }

    private fun getPendingIntent(intent: Intent, id: Int) =
        PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {

        println("ssssssssss ${pendingIntent.creatorUid} $pendingIntent")
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