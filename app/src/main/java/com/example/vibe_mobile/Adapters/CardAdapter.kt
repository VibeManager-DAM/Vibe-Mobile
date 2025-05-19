package com.example.vibe_mobile.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.vibe_mobile.Activities.ComprarEntradaUsuario
import com.example.vibe_mobile.Activities.ReservarEspacioGestor
import com.example.vibe_mobile.Clases.Event
import com.example.vibe_mobile.R
import com.example.vibe_mobile.Tools.Tools
import java.time.format.TextStyle
import java.util.Locale
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CardAdapter(
    private val context: Context,
        private var itemList: List<Event>
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fecha: TextView = itemView.findViewById(R.id.card_date)
        val titulo: TextView = itemView.findViewById(R.id.card_title)
        val precio: TextView = itemView.findViewById(R.id.card_price)
        val imagen: ImageView = itemView.findViewById(R.id.card_image)
        val boton: Button = itemView.findViewById(R.id.card_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = itemList[position]
        val fechaFormateada = formatearFecha(item.date)

        holder.fecha.text = fechaFormateada
        holder.titulo.text = item.title
        holder.precio.text = item.price.toString() + "€"
        holder.imagen.load(item.image) {
            transformations(RoundedCornersTransformation(10f))
        }

        holder.boton.setOnClickListener {
            val user = Tools.getUser(context) ?: return@setOnClickListener
            val context = holder.itemView.context

            val intent = when(user.id_rol) {
                2 -> Intent(context, ComprarEntradaUsuario::class.java)
                3 -> Intent(context, ComprarEntradaUsuario::class.java)
                else -> {
                    Toast.makeText(context, "Tu rol puede crear eventos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            intent.apply {
                putExtra("title", item.title)
                putExtra("date", item.date)
                putExtra("price", item.price)
                putExtra("description", item.description)
                putExtra("capacity", item.capacity)
                putExtra("image", item.image)
                putExtra("time", item.time)
                putExtra("id", item.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateData(newList: List<Event>) {
        itemList = newList
        notifyDataSetChanged()
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatearFecha(fechaOriginal: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fecha = LocalDateTime.parse(fechaOriginal, formatter)

        val dia = fecha.dayOfMonth.toString()
        val mes = fecha.month.getDisplayName(TextStyle.SHORT, Locale("es")).uppercase()
        return "$dia\n$mes"
    }

}
