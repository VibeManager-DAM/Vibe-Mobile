package com.example.vibe_mobile.Activities

import androidx.appcompat.app.AppCompatActivity
import com.example.vibe_mobile.R
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import coil.load

class ReservarEleccionGestor: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservar_espacio_eleccion)

        val image = intent.getStringExtra("image")
        findViewById<ImageView>(R.id.cardBuy_img).load(image) {
            error(R.drawable.icon_error)
        }

        val btn_puntual:AppCompatButton = findViewById(R.id.btn_puntual)
        val btn_periodoTiempo:AppCompatButton = findViewById(R.id.btn_periodoTiempo)
        val btn_back: AppCompatButton = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            finish()
        }

        btn_puntual.setOnClickListener{
//            val intent = Intent(context, ReservarEspacioGestor::class.java)
        }

        btn_periodoTiempo.setOnClickListener{

        }
    }
}