package mobileApp.recipecatalog.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class StepTimerService : Service() {

    companion object {
        const val CHANNEL_ID = "step_timer_channel"
        const val EXTRA_STEP_NUMBER = "extra_step_number"
        const val EXTRA_DURATION = "extra_duration_seconds"
        const val FOREGROUND_NOTIFICATION_ID = 1
    }

    private var timer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val stepNumber = intent?.getIntExtra(EXTRA_STEP_NUMBER, 0) ?: 0
        val durationSec = intent?.getIntExtra(EXTRA_DURATION, 0) ?: 0

        if (durationSec <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        // 🟢 Начальное foreground уведомление
        val initialNotification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер шага $stepNumber")
            .setContentText("Осталось $durationSec секунд")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()

        startForeground(FOREGROUND_NOTIFICATION_ID, initialNotification)

        // 🕒 CountDownTimer
        timer = object : CountDownTimer(durationSec * 1000L, 1000) {
            var secondsLeft = durationSec
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft--
                val updatedNotification = NotificationCompat.Builder(this@StepTimerService, CHANNEL_ID)
                    .setContentTitle("Таймер шага $stepNumber")
                    .setContentText("Осталось $secondsLeft секунд")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setOngoing(true)
                    .build()

                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(FOREGROUND_NOTIFICATION_ID, updatedNotification)
            }

            override fun onFinish() {
                // Сначала останавливаем foreground
                stopForeground(true)

                // Проигрываем звук окончания таймера
                try {
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val ringtone = RingtoneManager.getRingtone(applicationContext, notification)
                    ringtone.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Если хочешь, можно показать уведомление тоже
                val finishedNotification = NotificationCompat.Builder(this@StepTimerService, CHANNEL_ID)
                    .setContentTitle("Шаг $stepNumber завершён")
                    .setContentText("Время вышло!")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setAutoCancel(true)
                    .build()

                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(FOREGROUND_NOTIFICATION_ID + stepNumber, finishedNotification)

                stopSelf()
            }
        }.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о завершении шагов"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
