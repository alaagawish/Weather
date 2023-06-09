package eg.gov.iti.jets.kotlin.weather.alert.utils

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eg.gov.iti.jets.kotlin.weather.utils.Constants
import eg.gov.iti.jets.kotlin.weather.utils.Constants.CHANNEL_ID
import eg.gov.iti.jets.kotlin.weather.utils.Constants.CHANNEL_NAME
import eg.gov.iti.jets.kotlin.weather.MainActivity
import eg.gov.iti.jets.kotlin.weather.R

fun createNotificationChannel(context: Context, title: String, message: String) {

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val bigImage = BitmapFactory.decodeResource(
        context.resources, R.drawable.weather
    )
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(message)
        .setColor(context.getColor(R.color.light_blue))
        .setLargeIcon(bigImage)
        .setSmallIcon(R.drawable.weather)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .build()

    val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
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
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        println("createNotificationChannel: permission is given")
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)


    }
}

