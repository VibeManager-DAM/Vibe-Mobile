package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.vibe_mobile.FragmentActivity
import com.example.vibe_mobile.R


class ComprarEntradaUsuario: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comprar_entrada_usuario)

        // Recibimos los datos
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")
        val capacity = intent.getStringExtra("capacity")

        val btnComprarEntrada = findViewById<AppCompatButton>(R.id.btn_comprarEntrada)

        // Listener
        btnComprarEntrada.setOnClickListener {
            Toast.makeText(this,
                "Puedes ver tus entradas en tickets!",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}