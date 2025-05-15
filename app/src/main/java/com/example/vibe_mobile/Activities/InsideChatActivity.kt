package com.example.vibe_mobile.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.API.UploadImage.UploadImageRepository
import com.example.vibe_mobile.Adapters.MessageAdapter
import com.example.vibe_mobile.Clases.Message
import com.example.vibe_mobile.Clases.UploadImageResponse
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.ChatSocketService
import com.example.vibe_mobile.Tools.Tools
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class InsideChatActivity : AppCompatActivity() {

    companion object {
        private const val DEFAULT_CHAT_ID = -1
        private const val DEFAULT_USER_ID = -1
        private const val DEFAULT_RECEIVER_NAME = "Desconocido"
    }

    private lateinit var rootLayout: LinearLayout
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var eventNameChat: TextView
    private lateinit var backButton: ImageView
    private lateinit var addButton: ImageView
    private lateinit var adapter: MessageAdapter

    private lateinit var cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    private val REQUEST_PERMISSIONS = 3
    private var currentUserId: Int = DEFAULT_USER_ID
    private var chatId: Int = DEFAULT_CHAT_ID
    private val messages = mutableListOf<Message>()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null


    @RequiresApi(Build.VERSION_CODES.O)
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

        // Listeners de cámara y galería
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
                val selectedUri = result.data?.data
                selectedUri?.let {
                    val mimeType = contentResolver.getType(it)
                    val isImage = mimeType?.startsWith("image") == true
                    uploadImageToApi(it, isImage)
                }
            }
        }


        // Iniciar servicio de socket
        val serviceIntent = Intent(this, ChatSocketService::class.java).apply {
            putExtra("user_id", currentUserId)
            putExtra("chat_id", chatId)
        }
        startForegroundService(serviceIntent)

        // Recibir mensajes
        ChatSocketService.onNewMessage = { message ->
            runOnUiThread {
                messages.add(message)
                adapter.notifyItemInserted(messages.size - 1)
                chatRecyclerView.scrollToPosition(messages.size - 1)
            }
        }

        backButton.setOnClickListener { finish() }

        addButton.setOnClickListener {
            if (hasPermissions()) {
                showMediaOptions()
            } else {
                requestNecessaryPermissions()
            }
        }
    }

    private fun showMediaOptions() {
        val options = arrayOf("Tomar foto", "Elegir imagen", "Elegir video", "Grabar audio", "Elegir audio")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Seleccionar medio")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery(isVideo = false)
                2 -> openGallery(isVideo = true)
                3 -> recordAudio()
            }
        }
        builder.show()
    }


    private fun recordAudio() {
        if (!hasPermissions()) {
            requestNecessaryPermissions()
            return
        }

        var alertDialog: android.app.AlertDialog? = null

        val recordingButton = TextView(this).apply {
            text = "Comenzar grabación"
            textSize = 18f
            setPadding(50, 50, 50, 50)
            setOnClickListener {
                if (mediaRecorder == null) {
                    startRecording(this)
                } else {
                    stopRecording()
                    text = "Comenzar grabación"
                    alertDialog?.dismiss()
                }
            }
        }

        val dialogBuilder = android.app.AlertDialog.Builder(this)
            .setTitle("Grabar audio")
            .setView(recordingButton)
            .setCancelable(true)

        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }



    private fun startRecording(button: TextView) {
        val outputFile = File.createTempFile("audio_", ".3gp", cacheDir)
        audioFilePath = outputFile.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)

            try {
                prepare()
                start()
                button.text = "Detener grabación"
            } catch (e: Exception) {
                Log.e("RECORD", "Error al preparar MediaRecorder: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                Log.e("RECORD", "Error al detener grabación: ${e.message}")
            }
        }
        mediaRecorder = null

        audioFilePath?.let {
            val fileUri = Uri.fromFile(File(it))
            uploadImageToApi(fileUri, isImage = false)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaRecorder != null) {
            Log.w("RECORD", "Liberando MediaRecorder desde onStop")
            stopRecording()
        }
    }



    private fun uploadImageToApi(uri: Uri, isImage: Boolean) {
        var type = contentResolver.getType(uri)
        if (type == null) {
            type = "audio/3gpp"
        }

        val inputStream = contentResolver.openInputStream(uri) ?: return
        val requestFile = okhttp3.RequestBody.create(type.toMediaTypeOrNull(), inputStream.readBytes())

        val extension = when {
            type.startsWith("image/") -> when (type) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "image/gif" -> ".gif"
                else -> ".img"
            }
            type.startsWith("video/") -> when (type) {
                "video/mp4" -> ".mp4"
                "video/3gpp" -> ".3gp"
                else -> ".vid"
            }
            type.startsWith("audio/") -> when (type) {
                "audio/mpeg" -> ".mp3"
                "audio/wav" -> ".wav"
                "audio/3gpp" -> ".3gp"
                else -> ".aud"
            }
            else -> ".dat"
        }

        val fileName = "media_${System.currentTimeMillis()}$extension"
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

        UploadImageRepository().uploadImage(filePart).enqueue(object : retrofit2.Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val mediaUrl = response.body()!!.url
                    val prefix = when {
                        type.startsWith("image/") -> "IMG:"
                        type.startsWith("video/") -> "VID:"
                        type.startsWith("audio/") -> "AUD:"
                        else -> "FILE:"
                    }
                    sendMessage("$prefix$mediaUrl")
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                log("UPLOAD", "Error al subir archivo: ${t.message}")
            }
        })
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            Log.d("CAMERA", "abriendo camara")
            cameraLauncher.launch(intent)
        }
    }

    private fun openGallery(isVideo: Boolean) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = if (isVideo) "video/*" else "image/*"
        galleryLauncher.launch(intent)
    }


    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
                                           ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showMediaOptions()
            } else {
                android.widget.Toast.makeText(this, "Debes conceder todos los permisos", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun requestNecessaryPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO
                   )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
                   )
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

    private fun sendMessage(messageText: String) {
        ChatSocketService.sendMessage(messageText)
        messageInput.text.clear()
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
        stopService(Intent(this, ChatSocketService::class.java))
    }

    private fun log(tag: String, msg: String) {
        Log.d(tag, msg)
    }
}
