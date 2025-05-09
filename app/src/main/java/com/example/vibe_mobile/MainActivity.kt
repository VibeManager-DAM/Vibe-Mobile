package com.example.vibe_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vibe_mobile.Activities.CrearCuentaActivity
import com.example.vibe_mobile.Activities.IniciarSesionActivity
import com.example.vibe_mobile.Tools.CryptoUtils
import com.example.vibe_mobile.Tools.Tools
import com.example.vibe_mobile.API.Users.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autoLoginIfStored()

        val btnCrearCuenta: Button = findViewById(R.id.btn_crearCuenta)
        val btnIniciarSesion: TextView = findViewById(R.id.btn_iniciarSesion)

        btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, CrearCuentaActivity::class.java)
            startActivity(intent)
        }

        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesionActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun autoLoginIfStored() {
        val file = File(filesDir, "login.txt")
        if (!file.exists()){
            return
        }

        val lines = file.readLines()
        if (lines.size < 2) {
            return
        }

        val email = CryptoUtils.decrypt(lines[0])
        val password = CryptoUtils.decrypt(lines[2])

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.login(email, password)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!

                        Tools.saveUser(this@MainActivity, user)

                        val intent = Intent(this@MainActivity, FragmentActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Error en login automático", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
