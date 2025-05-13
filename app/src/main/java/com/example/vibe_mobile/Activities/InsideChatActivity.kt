package com.example.vibe_mobile.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.API.UploadImage.UploadImageRepository
import com.example.vibe_mobile.Adapters.MessageAdapter
import com.example.vibe_mobile.Clases.Message
import com.example.vibe_mobile.Clases.UploadImageResponse
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.CryptoUtils
import com.example.vibe_mobile.Tools.Tools
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
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
    private lateinit var addButton: ImageView
    private lateinit var socket: Socket
    private lateinit var outputStream: PrintWriter
    private lateinit var inputStream: BufferedReader
    private lateinit var adapter: MessageAdapter
    private lateinit var cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_PERMISSIONS = 3

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

        cameraLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                                                  ) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? android.graphics.Bitmap
                imageBitmap?.let {
                    val uri = Tools.saveBitmapToTempFile(this, it)
                    uri?.let { uploadImageToApi(it, isImage = true) }
                }
            }
        }

        galleryLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                                                   ) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    uploadImageToApi(it, isImage = true)
                }
            }
        }

        initViews(eventTitle)
        setUpRecycler()
        setupKeyboardListener()
        setupSendButton()

        backButton.setOnClickListener { finish() }

        connectToServer()

        addButton.setOnClickListener {
            if (hasPermissions()) {
                showMediaOptions()
            } else {
                requestNecessaryPermissions()
            }
        }
    }

    private fun reconnectToServer() {
        thread {
            var attempts = 0
            while (!isSocketConnected() && attempts < 5) {
                try {
                    log(TAG_SOCKET, "Intentando reconectar... intento $attempts")
                    socket = Socket(SERVER_IP, SERVER_PORT)
                    outputStream = PrintWriter(socket.getOutputStream(), true)
                    inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val authJson = JSONObject().apply {
                        put("sender_id", currentUserId)
                        put("chat_id", chatId)
                        put("content", "")
                    }
                    outputStream.println(authJson.toString())
                    receiveMessages()
                    log(TAG_SOCKET, "Reconexión exitosa")
                    return@thread

                } catch (e: Exception) {
                    log(TAG_SOCKET, "Fallo reconectando: ${e.message}")
                    Thread.sleep(1000)
                    attempts++
                }
            }
        }
    }

    private fun isSocketConnected(): Boolean {
        return ::socket.isInitialized && socket.isConnected && !socket.isClosed
    }

    private fun showMediaOptions() {
        val options = arrayOf("Tomar foto", "Elegir de la galería")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Seleccionar imagen")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun uploadImageToApi(uri: Uri, isImage: Boolean) {
        val type = contentResolver.getType(uri) ?: return
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val requestFile = okhttp3.RequestBody.create(type.toMediaTypeOrNull(), inputStream.readBytes())
        val extension = when (type) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/gif" -> ".gif"
            else -> ".dat"
        }
        val fileName = "media_${System.currentTimeMillis()}$extension"
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

        UploadImageRepository().uploadImage(filePart).enqueue(object : retrofit2.Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val mediaUrl = response.body()!!.url
                    val prefix = if (isImage) "IMG:" else "VID:"
                    sendMessage("$prefix$mediaUrl")
                } else {
                    log(TAG_SOCKET, "Fallo en respuesta upload: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                log(TAG_SOCKET, "Fallo al subir archivo: ${t.message}")
            }
        })
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestNecessaryPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermissions(permissions, REQUEST_PERMISSIONS)
    }

    private fun initViews(eventTitle: String) {
        rootLayout = findViewById(R.id.rootLayout)
        chatRecyclerView = findViewById(R.id.recyclerView)
        messageInput = findViewById(R.id.messageInput)
        eventNameChat = findViewById(R.id.nameChat)
        backButton = findViewById(R.id.backButton)
        addButton = findViewById(R.id.addButton)
        eventNameChat.text = eventTitle
    }

    private fun setUpRecycler() {
        adapter = MessageAdapter(messages, currentUserId)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = adapter
    }

    private fun setupSendButton() {
        val sendButton: ImageView = findViewById(R.id.sendButton)
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
                if (::socket.isInitialized && !socket.isClosed) {
                    socket.close()
                }
                socket = Socket(SERVER_IP, SERVER_PORT)
                outputStream = PrintWriter(socket.getOutputStream(), true)
                inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                val authJson = JSONObject().apply {
                    put("sender_id", currentUserId)
                    put("chat_id", chatId)
                    put("content", "")
                }
                outputStream.println(authJson.toString())

                runOnUiThread { log(TAG_SOCKET, "Conectado al servidor") }
                receiveMessages()
            } catch (e: Exception) {
                runOnUiThread { log(TAG_SOCKET, "Error al conectar: ${e.message}") }
            }
        }
    }

    private fun receiveMessages() {
        try {
            while (socket.isConnected) {
                val line = inputStream.readLine()
                if (line != null) {
                    processIncomingMessage(line)
                } else {
                    log(TAG_SOCKET, "Conexión perdida. Intentando reconectar...")
                    reconnectToServer()
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
            val encryptedContent = json.getString("content")
            val decryptedContent = CryptoUtils.decrypt(encryptedContent)

            val newMessage = Message(
                id = json.optInt("message_id", messages.size + 1),
                context = decryptedContent,
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
        thread {
            try {
                if (!isSocketConnected()) {
                    log(TAG_SOCKET, "Socket desconectado. Intentando reconexión...")
                    reconnectToServer()
                    runOnUiThread {
                        log(TAG_SOCKET, "Mensaje no enviado: reconectando...")
                    }
                    return@thread
                }

                var retries = 0
                while ((!::outputStream.isInitialized || outputStream.checkError()) && retries < 5) {
                    Thread.sleep(300)
                    retries++
                }

                if (!::outputStream.isInitialized || outputStream.checkError()) {
                    runOnUiThread {
                        log(TAG_SOCKET, "Socket aún no está listo para enviar mensaje")
                    }
                    return@thread
                }

                val encryptedText = CryptoUtils.encrypt(messageText)
                val messageJson = JSONObject().apply {
                    put("sender_id", currentUserId)
                    put("chat_id", chatId)
                    put("content", encryptedText)
                }

                outputStream.println(messageJson.toString())
                runOnUiThread { messageInput.text.clear() }

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
