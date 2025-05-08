package com.example.vibe_mobile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vibe_mobile.Clases.ChatPreview
import com.example.vibe_mobile.R

class ChatPreviewAdapter(
    private val chats: List<ChatPreview>,
    private val onClick: (ChatPreview) -> Unit
                        ) : RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtChatTitle)
        val image: ImageView = view.findViewById(R.id.imgChat)

        fun bind(chat: ChatPreview) {
            title.text = chat.eventTitle

            image.load(chat.image) {
                crossfade(true)
                error(R.drawable.icon_error)
            }

            itemView.setOnClickListener { onClick(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size
}
