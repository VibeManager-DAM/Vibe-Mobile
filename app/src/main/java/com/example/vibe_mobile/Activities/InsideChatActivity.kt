package com.example.vibe_mobile.Activities

import android.graphics.Rect
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Adapters.MessageAdapter
import com.example.vibe_mobile.Clases.Message
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.Tools

class InsideChatActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_CHAT_ID = -1
        private const val DEFAULT_USER_ID = -1
        private const val DEFAULT_RECEIVER_NAME = "Desconocido"
        private const val SERVER_IP = "10.0.3.148"
        private const val SERVER_PORT = 5000
        private const val TAG_SOCKET = "SOCKET"
    }

    private lateinit var rootLayout: LinearLayout
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var adapter: MessageAdapter
    private lateinit var eventNameChat: TextView
    private var currentUserId = Tools.getUser(this)?.id ?: DEFAULT_USER_ID

    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inside_chat)

        val currentUserId = Tools.getUser(this)?.id ?: DEFAULT_USER_ID
        val chatId = intent.getIntExtra("chat_id", DEFAULT_CHAT_ID)
        val eventTitle = intent.getStringExtra("event_title") ?: DEFAULT_RECEIVER_NAME
        val eventImage = intent.getStringExtra("event_image") ?: ""


        eventNameChat = findViewById(R.id.nameChat)
        eventNameChat.text = eventTitle

        initViews()
        setUpRecycler()
        setupKeyboardListener()
    }

    private fun setUpRecycler() {
        adapter = MessageAdapter(messages, currentUserId)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter
    }

    private fun initViews() {
        rootLayout = findViewById(R.id.rootLayout)
        chatRecyclerView = findViewById(R.id.recyclerView)
        messageInput = findViewById(R.id.messageInput)
        eventNameChat = findViewById(R.id.nameChat)
    }

    private fun setupKeyboardListener() {
        val messageContainer: LinearLayout = findViewById(R.id.messageContainer)
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootLayout.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootLayout.height
            val keypadHeight = screenHeight - rect.bottom

            messageContainer.translationY = if (keypadHeight > screenHeight * 0.15) {
                -keypadHeight.toFloat()
            } else {
                0f
            }
        }
    }
}