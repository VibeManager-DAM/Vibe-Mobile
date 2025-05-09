import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vibe_mobile.Clases.TicketItem
import com.example.vibe_mobile.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class TicketsAdapter(
    private var ticketList: List<TicketItem>) :
    RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.ticket_title)
        val time: TextView = itemView.findViewById(R.id.ticket_time)
        val day: TextView = itemView.findViewById(R.id.ticket_day)
        val image: ImageView = itemView.findViewById(R.id.ticket_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_item, parent, false)
        return TicketViewHolder(view)
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        val fechaFormateada = formatearFecha(ticket.date)
        val formatearHoraAmPm = formatearHoraAmPm(ticket.time)
        holder.title.text = ticket.title
        holder.time.text = formatearHoraAmPm
        holder.day.text = fechaFormateada
        holder.image.load(ticket.image)
    }

    override fun getItemCount(): Int = ticketList.size

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatearFecha(fechaOriginal: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fecha = LocalDateTime.parse(fechaOriginal, formatter)

        val dia = fecha.dayOfMonth.toString()
        val mes = fecha.month.getDisplayName(TextStyle.SHORT, Locale("es")).uppercase()
        return "$dia\n$mes"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatearHoraAmPm(horaOriginal: String): String {
        val formatterEntrada = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
        val formatterSalida = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()) // 12h + AM/PM
        val hora = LocalTime.parse(horaOriginal, formatterEntrada)
        return hora.format(formatterSalida)
    }
}
