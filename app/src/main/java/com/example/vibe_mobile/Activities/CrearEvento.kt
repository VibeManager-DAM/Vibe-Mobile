package com.example.vibe_mobile.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.vibe_mobile.R
import android.widget.Button
import android.widget.Toast
import com.example.vibe_mobile.Tools.Tools
import com.example.vibe_mobile.ViewModels.EventViewModel
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import com.example.vibe_mobile.Clases.Event
import com.example.vibe_mobile.Clases.EventCreate
import java.util.Locale

@Suppress("DEPRECATION")
class CrearEvento: AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val eventViewModel: EventViewModel by viewModels()
    private var image: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_evento)

        imageView = findViewById(R.id.crearEvento_img)
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        val crearEvento__title: EditText = findViewById(R.id.crearEvento__title)
        val crearEvento__description: EditText = findViewById(R.id.crearEvento__description)
        val crearEvento__date: EditText = findViewById(R.id.crearEvento__date)
        val crearEvento__time: EditText = findViewById(R.id.crearEvento__time)
        val crearEvento__capacity: EditText = findViewById(R.id.crearEvento__capacity)
        val crearEvento__price: EditText = findViewById(R.id.crearEvento__price)
        val crearEvento__btn: AppCompatButton = findViewById(R.id.crearEvento__btn)
        val user = Tools.getUser(this)

        val calendar = Calendar.getInstance()
        // Al pulsar en el campo de fecha
        crearEvento__date.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val dateFormatted = String.format("%04d-%02d-%02dT00:00:00", year, month + 1, dayOfMonth)
                crearEvento__date.setText(dateFormatted)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }

        // Al pulsar en el campo de hora
        crearEvento__time.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute)
                crearEvento__time.setText(timeFormatted)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePicker.show()
        }


        crearEvento__btn.setOnClickListener{
            val newEvent = EventCreate(
                title = crearEvento__title.text.toString(),
                description = crearEvento__description.text.toString(),
                date = crearEvento__date.text.toString(),
                time = crearEvento__time.text.toString(),
                image = image ?: "imagen_default.jpg",
                capacity = crearEvento__capacity.text.toString().toIntOrNull() ?: 0,
                seats = false,
                num_rows = 0,
                num_columns = 0,
                id_organizer = user?.id,
                price = crearEvento__price.text.toString().toDoubleOrNull() ?: 0.0
            )

            eventViewModel.createEvent(newEvent,
                onSuccess = {
                    Toast.makeText(this, "Evento creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { errorMsg ->
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            )
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)

            selectedImageUri?.let {
                image = getFileNameFromUri(it)
            }
        }
    }

    private fun getFileNameFromUri(uri: android.net.Uri): String? {
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            return it.getString(nameIndex)
        }
        return null
    }

}