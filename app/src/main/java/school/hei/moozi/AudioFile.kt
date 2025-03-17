package school.hei.moozi

data class AudioFile(
    val id: String,
    val title: String,
    val artist: String,
    val data: String, // Chemin du fichier audio
    val albumArt: String? = null // Pochette de l'album (si dispo)
)