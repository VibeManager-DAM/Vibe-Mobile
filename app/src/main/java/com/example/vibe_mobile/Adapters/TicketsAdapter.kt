import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Clases.TicketItem
import com.example.vibe_mobile.R
import java.time.format.DateTimeFormatter
import java.util.*

class TicketsAdapter(private val ticketList: List<TicketItem>) :
    RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.ticket_title)
        val time: TextView = itemView.findViewById(R.id.ticket_time)
        val day: TextView = itemView.findViewById(R.id.ticket_day)
        val month: TextView = itemView.findViewById(R.id.ticket_month)
        val image: ImageView = itemView.findViewById(R.id.ticket_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_item, parent, false)
        return TicketViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]

        holder.title.text = ticket.title

        // Formato 12h con AM/PM
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        holder.time.text = ticket.time.format(timeFormatter)

        holder.day.text = ticket.date.dayOfMonth.toString()

        // Mes abreviado con la primera letra en mayúscula
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
        holder.month.text = ticket.date.format(monthFormatter)

        holder.image.setImageResource(ticket.imageResId)
    }

    override fun getItemCount(): Int = ticketList.size
}
