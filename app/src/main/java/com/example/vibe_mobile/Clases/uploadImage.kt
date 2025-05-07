package com.example.vibe_mobile.Clases

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.load
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.UploadImage.UploadImageRepository
import com.example.vibe_mobile.MainActivity
import com.example.vibe_mobile.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class uploadImage : Fragment() {

    private lateinit var cameraImageUri: Uri
    private lateinit var card_image: ImageView
    private var imgURL = ""
    private lateinit var userImg: ImageView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (!granted) {
            Toast.makeText(context, "Permisos denegados", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        cameraImageUri = uri!!
        handleImageUri(uri)
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) handleImageUri(cameraImageUri)
    }

    private fun createImageUri(context: Context): Uri {
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File(imagesDir, "IMG_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    private fun handleImageUri(uri: Uri) {
        userImg.setImageURI(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.card_item, container, false)

        card_image = rootView.findViewById(R.id.card_image)

//        userImg = rootView.findViewById(R.id.ImgUserProfile)
//        userImg.load(MainActivity.UserSession.urlImg)
//        cameraImageUri = "".toUri()
//
//        val btnReturn = rootView.findViewById<ImageButton>(R.id.ImgBtnReturn)
//        val btnGalery = rootView.findViewById<Button>(R.id.BtnChangeImgGalery)
//        val btnCamera = rootView.findViewById<Button>(R.id.BtnChangeImgCamara)
//        val btnSave = rootView.findViewById<Button>(R.id.BtnSave)
//
//        btnReturn.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//
//        btnGalery.setOnClickListener {
//            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            pickImageLauncher.launch("image/*")
//        }
//
//        btnCamera.setOnClickListener {
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//            cameraImageUri = createImageUri(requireContext())
//            takePictureLauncher.launch(cameraImageUri)
//        }
//
//        btnSave.setOnClickListener {
//            uploadImageToServer(cameraImageUri, rootView)
//            requireActivity().supportFragmentManager.popBackStack()
//        }

        return rootView
    }

    private fun uploadImageToServer(imageUri: Uri?, view: View) {
        if (imageUri == null) {
            Toast.makeText(context, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        val file = getFileFromUri(view.context, imageUri)
        if (file == null || !file.exists()) {
            Toast.makeText(context, "Error al obtener el archivo", Toast.LENGTH_SHORT).show()
            return
        }

        val mimeType = view.context.contentResolver.getType(imageUri) ?: "image/*"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val repository = UploadImageRepository()

        repository.uploadImage(body).enqueue(object : Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.url.isNotEmpty()) {
                        imgURL = responseBody.url
                        Log.e("URL", imgURL)
                    } else {
                        Toast.makeText(view.context, "No se recibió URL de la imagen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("UPLOAD_ERROR", "Error en respuesta: ${response.code()} - $errorMessage")
                    Toast.makeText(view.context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                Log.e("UPLOAD_ERROR", "Fallo en la conexión: ${t.message}")
                Toast.makeText(view.context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val extension = when (context.contentResolver.getType(uri)) {
                "image/jpeg" -> ".jpg"
                "image/png" -> ".png"
                "image/gif" -> ".gif"
                else -> ".jpg"
            }

            val tempFile = File.createTempFile("temp_image", extension, context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("UPLOAD_ERROR", "Error al obtener archivo: ${e.message}")
            null
        }
    }
}
