package school.hei.moozi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import school.hei.moozi.R.id.audioArtist
import school.hei.moozi.R.id.audioTitle

class AudioAdapter(private val audioList: List<AudioFile>, private val onItemClick: (AudioFile) -> Unit) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(audioTitle)
        val artist: TextView = itemView.findViewById(audioArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }


    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioFile = audioList[position]
        holder.title.text = audioFile.title
        holder.artist.text = audioFile.artist

        holder.itemView.setOnClickListener {
            onItemClick(audioFile)  // Appelle la fonction lors d’un clic
        }
    }

    override fun getItemCount() = audioList.size
}