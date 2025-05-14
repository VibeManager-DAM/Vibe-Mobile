package com.example.vibe_mobile.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vibe_mobile.Clases.Message
import com.example.vibe_mobile.R

class MessageAdapter(
    private val messages: List<Message>,
    private val currentUserId: Int
                    ) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val content = message.context

        // Resetear visibilidades
        holder.sentText.visibility = View.GONE
        holder.sentImage.visibility = View.GONE
        holder.sentVideo.visibility = View.GONE
        holder.receivedText.visibility = View.GONE
        holder.receivedImage.visibility = View.GONE
        holder.receivedVideo.visibility = View.GONE

        if (message.sender_id == currentUserId) {
            holder.sentMessageLayout.visibility = View.VISIBLE
            holder.receivedMessageLayout.visibility = View.GONE

            when {
                content.startsWith("IMG:") -> {
                    val url = content.removePrefix("IMG:")
                    holder.sentImage.visibility = View.VISIBLE
                    loadImage(holder.sentImage, url)
                }

                content.startsWith("VID:") -> {
                    val url = content.removePrefix("VID:")
                    holder.sentVideo.visibility = View.VISIBLE
                    loadVideo(holder.sentVideo, url)
                }

                content.startsWith("AUD:") -> {
                    val url = content.removePrefix("AUD:")
                    holder.sentAudio.visibility = View.VISIBLE
                    holder.sentAudio.setOnClickListener {
                        playAudio(url)
                    }
                }

                else -> {
                    holder.sentText.visibility = View.VISIBLE
                    holder.sentText.text = content
                }
            }
        } else {
            holder.sentMessageLayout.visibility = View.GONE
            holder.receivedMessageLayout.visibility = View.VISIBLE

            when {
                content.startsWith("IMG:") -> {
                    val url = content.removePrefix("IMG:")
                    holder.receivedImage.visibility = View.VISIBLE
                    loadImage(holder.receivedImage, url)
                }

                content.startsWith("VID:") -> {
                    val url = content.removePrefix("VID:")
                    holder.receivedVideo.visibility = View.VISIBLE
                    loadVideo(holder.receivedVideo, url)
                }

                content.startsWith("AUD:") -> {
                    val url = content.removePrefix("AUD:")
                    holder.receivedAudio.visibility = View.VISIBLE
                    holder.receivedAudio.setOnClickListener {
                        playAudio(url)
                    }
                }

                else -> {
                    holder.receivedText.visibility = View.VISIBLE
                    holder.receivedText.text = content
                }
            }
        }
    }

    private fun loadImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    private fun loadVideo(videoView: VideoView, url: String) {
        videoView.setVideoURI(Uri.parse(url))
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
        videoView.setOnClickListener {
            if (videoView.isPlaying) videoView.pause()
            else videoView.start()
        }
    }

    private fun playAudio(url: String) {
        val mediaPlayer = android.media.MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
        }
    }



    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessageLayout: FrameLayout = itemView.findViewById(R.id.receivedMessageLayout)
        val sentMessageLayout: FrameLayout = itemView.findViewById(R.id.sentMessageLayout)

        val receivedText: TextView = itemView.findViewById(R.id.receivedText)
        val sentText: TextView = itemView.findViewById(R.id.sentText)

        val receivedImage: ImageView = itemView.findViewById(R.id.receivedImage)
        val sentImage: ImageView = itemView.findViewById(R.id.sentImage)

        val receivedVideo: VideoView = itemView.findViewById(R.id.receivedVideo)
        val sentVideo: VideoView = itemView.findViewById(R.id.sentVideo)

        val receivedAudio: ImageView = itemView.findViewById(R.id.receivedAudio)
        val sentAudio: ImageView = itemView.findViewById(R.id.sentAudio)

    }
}
