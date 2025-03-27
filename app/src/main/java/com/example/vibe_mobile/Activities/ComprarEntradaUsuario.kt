package com.example.vibe_mobile.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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



    }
}