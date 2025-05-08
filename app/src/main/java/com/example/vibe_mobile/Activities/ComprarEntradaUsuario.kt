package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import coil.load
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
        val image = intent.getStringExtra("image")
        val time = intent.getStringExtra("time")

        findViewById<TextView>(R.id.cardBuy_title).text = title
        findViewById<TextView>(R.id.cardBuy_description).text = description
        findViewById<TextView>(R.id.cardBuy_date).text = date
        findViewById<TextView>(R.id.cardBuy_price).text = price
        findViewById<TextView>(R.id.cardBuy_time).text = time
        findViewById<ImageView>(R.id.cardBuy_img).load(image) {
            error(R.drawable.icon_error)
        }

        val btnComprarEntrada = findViewById<AppCompatButton>(R.id.cardBuy_btn)

        // Agregar una animacion de gracias por comprar con nosotros o algo asi.
        // Listener
        btnComprarEntrada.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            intent.putExtra("ticketFragment", "tickets")
            intent.putExtra("ticketEventTitle", title)
            intent.putExtra("ticketEventImage", image)
            startActivity(intent)
            finish()
        }
    }
}