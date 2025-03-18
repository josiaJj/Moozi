package school.hei.moozi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentFilePath: String? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("ACTION")

        when (action) {
            "PLAY" -> resumeAudio()
            "PAUSE" -> pauseAudio()
            "STOP" -> stopAudio()
            else -> {
                val audioFile = intent?.getStringExtra("AUDIO_FILE")
                if (audioFile != null && audioFile != currentFilePath) {
                    playAudio(audioFile)
                }
            }
        }

        return START_STICKY
    }

    private fun playAudio(filePath: String) {
        stopAudio() // Assure qu'aucune autre musique ne joue

        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            setOnCompletionListener { stopSelf() }
        }

        currentFilePath = filePath
        isPlaying = true
        showNotification()
    }

    private fun resumeAudio() {
        mediaPlayer?.start()
        isPlaying = true
        showNotification()
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        showNotification()
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "MUSIC_CHANNEL",
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play

        val notification = NotificationCompat.Builder(this, "MUSIC_CHANNEL")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle("Musique en cours")
            .setContentText("Artiste inconnu")
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
