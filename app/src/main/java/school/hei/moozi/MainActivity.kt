package school.hei.moozi

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var playPauseButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private var isPlaying = false
    private var currentIndex = 0
    private lateinit var audioList: List<AudioFile>
    private var mediaPlayer: MediaPlayer? = null

    private fun playAudio(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
            playPauseButton.text = "⏸" // Change l’icône en pause

            setOnCompletionListener {
                playNext() // Passe à la musique suivante automatiquement
            }
        }
        isPlaying = true // Déplacer cette ligne ici
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        playPauseButton.text = "▶"
    }

    private fun resumeAudio() {
        mediaPlayer?.start()
        isPlaying = true
        playPauseButton.text = "⏸"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialisation des boutons de contrôle
        playPauseButton = findViewById(R.id.playPauseButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        audioList = getAudioFiles() // Récupère les fichiers audio

        recyclerView.adapter = AudioAdapter(audioList) { audioFile ->
            currentIndex = audioList.indexOf(audioFile) // Met à jour l'index courant
            playAudio(audioFile.data)  // Joue la musique au clic
        }

        // Gestion des boutons
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseAudio()
            } else {
                resumeAudio()
            }
        }

        prevButton.setOnClickListener { playPrevious() }
        nextButton.setOnClickListener { playNext() }
    }

    private fun getAudioFiles(): List<AudioFile> {
        val audioList = mutableListOf<AudioFile>()
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )

        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getString(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn) ?: "Inconnu"
                val data = it.getString(dataColumn)

                audioList.add(AudioFile(id, title, artist, data))
            }
        }

        return audioList
    }
}