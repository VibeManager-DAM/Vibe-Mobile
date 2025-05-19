package com.example.vibe_mobile.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import coil.load
import com.example.vibe_mobile.FragmentActivity
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.Tools


class ReservarEspacioGestor: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservar_espacio_gestor)

        val user = Tools.getUser(this) ?: return

        val btn_back: AppCompatButton = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            finish()
        }

        // Recibimos los datos
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val price = intent.getDoubleExtra("price", 0.00)
        val description = intent.getStringExtra("description")
        val capacity = intent.getIntExtra("capacity",-1)
        val image = intent.getStringExtra("image")
        val time = intent.getStringExtra("time")
        val eventId = intent.getIntExtra("id", -1)
        val userId = user.id

        findViewById<TextView>(R.id.cardBuy_title).text = title
        findViewById<TextView>(R.id.cardBuy_description).text = description
        findViewById<TextView>(R.id.cardBuy_date).text = date
        findViewById<TextView>(R.id.cardBuy_time).text = time
        findViewById<TextView>(R.id.cardBuy_capacidad).text = capacity.toString()
        findViewById<TextView>(R.id.cardBuy_price).text = price.toString() + "€"
        findViewById<ImageView>(R.id.cardBuy_img).load(image) {
            error(R.drawable.icon_error)
        }

        val btnReservarEspacio = findViewById<AppCompatButton>(R.id.cardBuy_btn)

        btnReservarEspacio.setOnClickListener{
            val intent = Intent(this, ReservarEleccionGestor::class.java)
            intent.putExtra("image", image)
            startActivity(intent)
            finish()
        }
    }
}