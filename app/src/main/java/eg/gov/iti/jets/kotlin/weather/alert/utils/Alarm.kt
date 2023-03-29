package eg.gov.iti.jets.kotlin.weather.alert.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eg.gov.iti.jets.kotlin.weather.Constants
import eg.gov.iti.jets.kotlin.weather.Constants.ACTION_SET_REPETITIVE_EXACT
import eg.gov.iti.jets.kotlin.weather.Constants.NOTIFICATION_ID
import eg.gov.iti.jets.kotlin.weather.MainActivity
import eg.gov.iti.jets.kotlin.weather.R
import eg.gov.iti.jets.kotlin.weather.alert.receiver.AlarmReceiver

@SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
fun createAlarmChannel(context: Context, title: String, content: String) {

    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    val bigImage = BitmapFactory.decodeResource(
        context.resources, R.drawable.weather
    )
    val bigPicStyle =
        NotificationCompat.BigPictureStyle().bigPicture(bigImage).bigLargeIcon(null)

    val playIntent = Intent(context, AlarmReceiver::class.java)
    playIntent.action = ACTION_SET_REPETITIVE_EXACT
    val pendingPlayIntent =
        PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
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

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {

        return
    }
    val actionIntent = Intent(context, AlarmReceiver::class.java)
    actionIntent.action = context.getString(R.string.dismiss)
    val dismissIntent =
        PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            actionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
        .setSmallIcon(R.drawable.weather)
        .setContentTitle(title)
        .setContentText(content)
        .setContentIntent(pendingPlayIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle).setLargeIcon(bigImage)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .addAction(
            R.drawable.baseline_repeat_24, context.getString(R.string.repeat),
            pendingPlayIntent
        )
        .addAction(
            R.drawable.baseline_cancel_presentation_24, context.getString(R.string.dismiss),
            dismissIntent
        )

    notificationManager.notify(NOTIFICATION_ID, notification.build())


}