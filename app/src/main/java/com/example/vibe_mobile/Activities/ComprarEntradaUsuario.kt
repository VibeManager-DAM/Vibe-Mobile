package com.example.vibe_mobile.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.Tickets.TicketService
import com.example.vibe_mobile.Clases.ReserveTicket
import com.example.vibe_mobile.FragmentActivity
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.Tools
import kotlinx.coroutines.launch


class ComprarEntradaUsuario: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comprar_entrada_usuario)
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
        findViewById<TextView>(R.id.cardBuy_price).text = price.toString() + "€"
        findViewById<TextView>(R.id.cardBuy_time).text = time
        findViewById<ImageView>(R.id.cardBuy_img).load(image) {
            error(R.drawable.icon_error)
        }

        val btnComprarEntrada = findViewById<AppCompatButton>(R.id.cardBuy_btn)

        // Listener
        btnComprarEntrada.setOnClickListener {
            val ReserveTicket = ReserveTicket(
                Date = date ?: "",
                Time = time ?: "",
                NumRow = 0,       // Puedes cambiarlo por selección del usuario
                NumCol = 0,       // Igual que arriba
                IdEvent = eventId?.toInt(),
                IdUser = userId
            )

            lifecycleScope.launch {
                val intent = Intent(this@ComprarEntradaUsuario, FragmentActivity::class.java)
                try {
                    val response = RetrofitClient.createService(TicketService::class.java).reserveTicket(ReserveTicket)
                    if (response.isSuccessful) {
                        Toast.makeText(this@ComprarEntradaUsuario, "Entrada comprada correctamente", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ComprarEntradaUsuario, "Error al comprar la entrada", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ComprarEntradaUsuario, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}