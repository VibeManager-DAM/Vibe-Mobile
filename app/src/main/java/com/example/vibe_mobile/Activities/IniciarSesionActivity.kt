package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.vibe_mobile.FragmentActivity
import com.example.vibe_mobile.R

class IniciarSesionActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incio_sesion)

        val btnIniciar = findViewById<AppCompatButton>(R.id.btn_iniciarSesion)

        // Listener
        btnIniciar.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}