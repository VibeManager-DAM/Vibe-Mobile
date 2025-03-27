import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Activities.ComprarEntradaUsuario
import com.example.vibe_mobile.Clases.CardItem
import com.example.vibe_mobile.R

class CardAdapter(
    private val itemList: List<CardItem>
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

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = itemList[position]
        holder.fecha.text = item.date
        holder.titulo.text = item.title
        holder.precio.text = item.price
        holder.imagen.setImageResource(item.imagenResId)

        holder.boton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ComprarEntradaUsuario::class.java).apply {
                putExtra("title", item.title)
                putExtra("date", item.date)
                putExtra("price", item.price)
                putExtra("description", item.description)
                putExtra("capacity", item.capacity)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
