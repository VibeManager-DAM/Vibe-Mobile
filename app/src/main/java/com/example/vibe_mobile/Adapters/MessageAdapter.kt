package com.example.vibe_mobile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        if (message.sender_id == currentUserId) {
            // Mensaje enviado por el usuario actual
            holder.sentMessageLayout.visibility = View.VISIBLE
            holder.receivedMessageLayout.visibility = View.GONE
            holder.sentText.text = message.context
        } else {
            // Mensaje recibido
            holder.receivedMessageLayout.visibility = View.VISIBLE
            holder.sentMessageLayout.visibility = View.GONE
            holder.receivedText.text = message.context
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessageLayout: FrameLayout = itemView.findViewById(R.id.receivedMessageLayout)
        val sentMessageLayout: FrameLayout = itemView.findViewById(R.id.sentMessageLayout)

        val receivedText: TextView = itemView.findViewById(R.id.receivedText)
        val sentText: TextView = itemView.findViewById(R.id.sentText)
    }
}
