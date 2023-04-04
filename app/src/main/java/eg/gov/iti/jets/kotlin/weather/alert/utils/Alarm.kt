package eg.gov.iti.jets.kotlin.weather.alert.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.NOTIFICATION_ID
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.receiver.AlarmReceiver
import eg.gov.iti.jets.kotlin.weather.utils.Constants.ACTION_SET_REPETITIVE_EXACT

@SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
fun createAlarmChannel(context: Context, title: String, content: String, id: Long) {


    val playIntent = Intent(context, AlarmReceiver::class.java)
    playIntent.action = ACTION_SET_REPETITIVE_EXACT
    val snoozeIntent =
        PendingIntent.getBroadcast(
            context,
            id.toInt(),
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    val channel = NotificationChannel(
        Constants.CHANNEL_ID,
        Constants.CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        lightColor = Color.BLUE
        enableLights(true)
    }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
    val notificationManager = NotificationManagerCompat.from(context)


    val actionIntent = Intent(context, AlarmReceiver::class.java)
    actionIntent.action = Constants.ALARM_ACTION
    val dismissIntent =
        PendingIntent.getBroadcast(
            context,
            1,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        .setSmallIcon(R.drawable.weather)
        .setContentTitle(title)
        .setContentText(content)
        .setContentIntent(snoozeIntent)
        .setContentIntent(dismissIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .addAction(
            R.drawable.baseline_repeat_24, context.getString(R.string.snooze),
            snoozeIntent
        )
        .addAction(
            R.drawable.baseline_cancel_presentation_24, context.getString(R.string.stop),
            dismissIntent
        )

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        notificationManager.notify(NOTIFICATION_ID, notification.build())

    }

}