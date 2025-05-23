package com.example.finsight20.util


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.finsight20.R

object NotificationUtils {
    fun gonder(context: Context, baslik: String, mesaj: String) {

        // Android 13 ve üzeri için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val izin = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (izin != PackageManager.PERMISSION_GRANTED) {
                Log.w("Bildirim", "Bildirim izni yok, bildirim gönderilmiyor.")
                return
            }
        }

        val kanalId = "tahmin_bildirim_kanali"
        val kanal = NotificationChannel(
            kanalId,
            "Tahmin Bildirimleri",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Belirlenen tahmini bütçe aşıldığında uyarı gönderir."
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(kanal)

        val builder = NotificationCompat.Builder(context, kanalId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.finsight_logo))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(baslik)
            .setContentText(mesaj)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }
}