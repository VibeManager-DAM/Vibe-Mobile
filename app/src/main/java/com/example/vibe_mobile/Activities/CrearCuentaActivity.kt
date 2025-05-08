package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.vibe_mobile.Clases.User
import com.example.vibe_mobile.R
import com.example.vibe_mobile.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearCuentaActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_cuenta)

        val btnCrear = findViewById<AppCompatButton>(R.id.CreateAccount)
        val nameField = findViewById<EditText>(R.id.user_name)
        val emailField = findViewById<EditText>(R.id.user_mail)
        val passwordField = findViewById<EditText>(R.id.user_password)

        btnCrear.setOnClickListener {
            val fullname = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(fullname = fullname, email = email, password = password, id_rol = 2)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = userRepository.registerUser(newUser)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            Toast.makeText(this@CrearCuentaActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CrearCuentaActivity, IniciarSesionActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@CrearCuentaActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CrearCuentaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}