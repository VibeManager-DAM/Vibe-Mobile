package com.example.vibe_mobile.Tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.vibe_mobile.Clases.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class ChatSocketService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var socket: Socket? = null
    private var outputStream: PrintWriter? = null
    private var inputStream: BufferedReader? = null

    private var currentUserId = -1
    private var chatId = -1

    companion object {
        private const val CHANNEL_ID = "ChatSocketServiceChannel"
        private const val SERVER_IP = "10.0.3.148"
        private const val SERVER_PORT = 9696

        var onNewMessage: ((Message) -> Unit)? = null
        var onLog: ((String) -> Unit)? = null

        fun isConnected(): Boolean {
            return instance?.socket?.isConnected == true && instance?.socket?.isClosed == false
        }

        fun sendMessage(content: String) {
            instance?.let {
                if (it.socket?.isConnected == true && it.socket?.isClosed == false) {
                    it.serviceScope.launch {
                        val encrypted = CryptoUtils.encrypt(content)
                        val messageJson = JSONObject().apply {
                            put("sender_id", it.currentUserId)
                            put("chat_id", it.chatId)
                            put("content", encrypted)
                        }
                        it.outputStream?.println(messageJson.toString())
                    }
                } else {
                    onLog?.invoke("[Service] No se puede enviar mensaje: socket desconectado")
                }
            }
        }



        private var instance: ChatSocketService? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vibe Chat")
                .setContentText("Conectado al servidor de chat")
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentUserId = intent?.getIntExtra("user_id", -1) ?: -1
        chatId = intent?.getIntExtra("chat_id", -1) ?: -1
        connectSocket()
        return START_STICKY
    }

    private fun connectSocket() {
        serviceScope.launch {
            try {
                socket = Socket(SERVER_IP, SERVER_PORT)
                outputStream = PrintWriter(socket!!.getOutputStream(), true)
                inputStream = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                val authJson = JSONObject().apply {
                    put("sender_id", currentUserId)
                    put("chat_id", chatId)
                    put("content", "")
                }
                outputStream?.println(authJson.toString())

                onLog?.invoke("[Service] Conectado al servidor")
                listenForMessages()
            } catch (e: Exception) {
                onLog?.invoke("[Service] Error al conectar: ${e.message}")
            }
        }
    }

    private fun listenForMessages() {
        serviceScope.launch {
            try {
                while (socket?.isConnected == true) {
                    val line = inputStream?.readLine() ?: break
                            val json = JSONObject(line)
                    val decrypted = CryptoUtils.decrypt(json.getString("content"))
                    val message = Message(
                            id = json.optInt("message_id"),
                            context = decrypted,
                            send_at = json.optString("timestamp", getCurrentTimestamp()),
                            sender_id = json.getInt("from"),
                            id_chat = chatId
                    )
                    onNewMessage?.invoke(message)
                }
            } catch (e: Exception) {
                onLog?.invoke("[Service] Error recibiendo mensaje: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            socket?.close()
            onLog?.invoke("[Service] Socket cerrado")
        } catch (e: Exception) {
            onLog?.invoke("[Service] Error cerrando socket: ${e.message}")
        }
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Chat Socket Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
