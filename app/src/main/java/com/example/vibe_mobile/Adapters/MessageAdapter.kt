package com.example.vibe_mobile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Activities.InsideChatActivity
import com.example.vibe_mobile.Clases.Message
import com.example.vibe_mobile.R

class MessageAdapter(private val messages: List<Message>,
                    ) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val additionalContentMap = mutableMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {/*
    }
        val receivedMessageLayout: FrameLayout = itemView.findViewById(R.id.receivedMessageLayout)
        val sentMessageLayout: FrameLayout = itemView.findViewById(R.id.sentMessageLayout)

        val receivedEventLayout: FrameLayout = itemView.findViewById(R.id.receivedEventLayout)
        val sentEventLayout: FrameLayout = itemView.findViewById(R.id.sentEventLaout)
        val acceptEventByUser: LinearLayout = itemView.findViewById(R.id.acceptedByUser)
        val refuseEventByUser: LinearLayout = itemView.findViewById(R.id.refusedByUser)
        val decideEventUser: LinearLayout = itemView.findViewById(R.id.decideEventUser)
        val acceptEventByUserEnviado: LinearLayout = itemView.findViewById(R.id.acceptedByUserEnviado)
        val refuseEventByUserEnviado: LinearLayout = itemView.findViewById(R.id.refusedByUserEnviado)

        val eventText: TextView = itemView.findViewById(R.id.sentEventText)
        val fechaText: TextView = itemView.findViewById(R.id.textViewFecha)
        val checkContrato: CheckBox = itemView.findViewById(R.id.checkContrato)

        val eventTextEnviado: TextView = itemView.findViewById(R.id.sentEventTextEnviado)
        val fechaTextEnviado: TextView = itemView.findViewById(R.id.textViewFechaEnviado)
        val checkContratoEnviado: CheckBox = itemView.findViewById(R.id.checkContratoEnviado)

        val acceptButton: ImageView = itemView.findViewById(R.id.acceptEventButton)
        val discardButton: ImageView = itemView.findViewById(R.id.sentEventButton)

        val receivedText: TextView = itemView.findViewById(R.id.receivedText)
        val sentText: TextView = itemView.findViewById(R.id.sentText)

        val receivedImage: ImageView = itemView.findViewById(R.id.receivedImage)
        val sentImage: ImageView = itemView.findViewById(R.id.sentImage)

         */
    }
}