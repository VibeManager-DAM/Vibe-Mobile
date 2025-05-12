package com.example.vibe_mobile.Activities

import android.graphics.Rect
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

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
    private lateinit var eventNameChat: TextView
    private lateinit var backButton: ImageView
    private lateinit var socket: Socket
    private lateinit var outputStream: PrintWriter
    private lateinit var inputStream: BufferedReader
    private lateinit var adapter: MessageAdapter

    private var currentUserId: Int = DEFAULT_USER_ID
    private var chatId: Int = DEFAULT_CHAT_ID
    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inside_chat)

        currentUserId = Tools.getUser(this)?.id ?: DEFAULT_USER_ID
        chatId = intent.getIntExtra("chat_id", DEFAULT_CHAT_ID)
        val eventTitle = intent.getStringExtra("event_title") ?: DEFAULT_RECEIVER_NAME

        initViews(eventTitle)
        setUpRecycler()
        setupKeyboardListener()
        setupSendButton()

        backButton.setOnClickListener { finish() }

        connectToServer()
    }

    private fun initViews(eventTitle: String) {
        rootLayout = findViewById(R.id.rootLayout)
        chatRecyclerView = findViewById(R.id.recyclerView)
        messageInput = findViewById(R.id.messageInput)
        eventNameChat = findViewById(R.id.nameChat)
        backButton = findViewById(R.id.backButton)

        eventNameChat.text = eventTitle
    }

    private fun setUpRecycler() {
        adapter = MessageAdapter(messages, currentUserId)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter
    }

    private fun setupSendButton() {
        val sendButton: ImageView = findViewById(R.id.sendButton) // Asegúrate de tener este ID
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotBlank()) {
                sendMessage(messageText)
            }
        }
    }

    private fun connectToServer() {
        thread(start = true) {
            try {
                socket = Socket(SERVER_IP, SERVER_PORT)
                outputStream = PrintWriter(socket.getOutputStream(), true)
                inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                val authJson = JSONObject().apply {
                    put("sender_id", currentUserId)
                    put("chat_id", chatId)
                    put("content", "") // solo para cumplir con estructura
                }
                outputStream.println(authJson.toString())

                runOnUiThread {
                    log(TAG_SOCKET, "Conectado al servidor")
                }

                receiveMessages()

            } catch (e: Exception) {
                runOnUiThread {
                    log(TAG_SOCKET, "Error al conectar: ${e.message}")
                }
            }
        }
    }

    private fun receiveMessages() {
        try {
            while (socket.isConnected) {
                val line = inputStream.readLine() // Asignamos el valor a 'line' aquí
                if (line != null) {
                    processIncomingMessage(line)
                } else {
                    // Si se ha recibido null, significa que se cerró la conexión
                    break
                }
            }
        } catch (e: Exception) {
            log(TAG_SOCKET, "Error recibiendo datos: ${e.message}")
        }
    }


    private fun processIncomingMessage(messageStr: String) {
        try {
            val json = JSONObject(messageStr)

            val newMessage = Message(
                id = json.optInt("message_id", messages.size + 1),
                context = json.getString("content"),
                send_at = json.optString("send_at", getCurrentTimestamp()),
                sender_id = json.getInt("from"),
                id_chat = chatId
                                    )

            runOnUiThread {
                messages.add(newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                chatRecyclerView.scrollToPosition(messages.size - 1)
            }

        } catch (e: Exception) {
            log(TAG_SOCKET, "Error procesando mensaje: ${e.message}")
        }
    }


    private fun sendMessage(messageText: String) {
        val messageJson = JSONObject().apply {
            put("sender_id", currentUserId)
            put("chat_id", chatId)
            put("content", messageText)
        }

        thread {
            try {
                outputStream.println(messageJson.toString())
                runOnUiThread {
                    messageInput.text.clear() // Limpiar input solo
                }
            } catch (e: Exception) {
                log(TAG_SOCKET, "Error enviando mensaje: ${e.message}")
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::socket.isInitialized && !socket.isClosed) {
                socket.close()
            }
        } catch (e: Exception) {
            log(TAG_SOCKET, "Error cerrando socket: ${e.message}")
        }
    }

    private fun getCurrentTimestamp(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }


    private fun log(tag: String, msg: String) {
        android.util.Log.d(tag, msg)
    }
}
