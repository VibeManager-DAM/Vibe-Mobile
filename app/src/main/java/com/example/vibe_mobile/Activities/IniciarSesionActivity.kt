package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.vibe_mobile.FragmentActivity
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.CryptoUtils
import com.example.vibe_mobile.Tools.Tools
import com.example.vibe_mobile.API.Users.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class IniciarSesionActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incio_sesion)

        val btnStart = findViewById<AppCompatButton>(R.id.btn_iniciarSesion)
        val checkRemember = findViewById<CheckBox>(R.id.checkbox_remember)
        val emailField = findViewById<EditText>(R.id.user_correo)
        val passwordField = findViewById<EditText>(R.id.user_contrasena)
        val btnCreateAccount = findViewById<TextView>(R.id.btn_crearCuenta)

        btnCreateAccount.setOnClickListener{
            val intent = Intent(this, CrearCuentaActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnStart.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = userRepository.login(email, password)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val user = response.body()!!
                            if (user.id_rol != 3){
                                Tools.saveUser(this@IniciarSesionActivity, user)

                                if (checkRemember.isChecked) {
                                    val file = File(filesDir, "login.txt")
                                    val encryptedEmail = CryptoUtils.encrypt(email)
                                    val encryptedPassword = CryptoUtils.encrypt(password)
                                    file.writeText("$encryptedEmail\n$encryptedPassword")
                                }

                                Toast.makeText(this@IniciarSesionActivity, "Bienvenido, ${user.fullname}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@IniciarSesionActivity, FragmentActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else{
                                Toast.makeText(this@IniciarSesionActivity, "No puedes acceder como administrador", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@IniciarSesionActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@IniciarSesionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
