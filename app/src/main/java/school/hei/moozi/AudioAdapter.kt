package school.hei.moozi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter(private val audioList: List<AudioFile>) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    class AudioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val artist: TextView = view.findViewById(R.id.textArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = audioList[position]
        holder.title.text = audio.title
        holder.artist.text = audio.artist
    }

    override fun getItemCount(): Int = audioList.size
}