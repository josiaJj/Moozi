package school.hei.moozi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentIndex = 0
    private lateinit var audioList: List<AudioFile>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()

        sharedPreferences = getSharedPreferences("MUSIC_PREFS", MODE_PRIVATE)
        isPlaying = sharedPreferences.getBoolean("IS_PLAYING", false) // 🔥 Charger l'état
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("ACTION")

        when (action) {
            "PLAY" -> resumeAudio()
            "PAUSE" -> pauseAudio()
            "NEXT" -> playNext()
            "PREVIOUS" -> playPrevious()
            else -> {
                val audioFile = intent?.getStringExtra("AUDIO_FILE")
                if (audioFile != null) {
                    playAudio(audioFile)
                }
            }
        }

        return START_STICKY
    }

    private fun playAudio(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            isPlaying = true
            savePlaybackState(isPlaying) // 🔥 Sauvegarde état
            setOnCompletionListener { stopSelf() }
        }
        showNotification()
    }

    private fun resumeAudio() {
        mediaPlayer?.start()
        isPlaying = true
        savePlaybackState(isPlaying)
        showNotification()
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        savePlaybackState(isPlaying)
        showNotification()
    }

    private fun playNext() {
        if (audioList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % audioList.size
            playAudio(audioList[currentIndex].data)
        }
    }

    private fun playPrevious() {
        if (audioList.isNotEmpty()) {
            currentIndex = if (currentIndex - 1 < 0) audioList.size - 1 else currentIndex - 1
            playAudio(audioList[currentIndex].data)
        }
    }

    private fun savePlaybackState(isPlaying: Boolean) {
        sharedPreferences.edit().putBoolean("IS_PLAYING", isPlaying).apply()
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
            .addAction(R.drawable.ic_previous, "Précédent", null) // TODO: Ajouter PendingIntent
            .addAction(playPauseIcon, if (isPlaying) "Pause" else "Play", null) // TODO: Ajouter PendingIntent
            .addAction(R.drawable.ic_next, "Suivant", null) // TODO: Ajouter PendingIntent
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
