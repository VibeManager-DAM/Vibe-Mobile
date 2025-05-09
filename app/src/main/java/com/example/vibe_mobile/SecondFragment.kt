package com.example.vibe_mobile

import TicketsAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.Users.UserService
import com.example.vibe_mobile.Clases.Ticket
import com.example.vibe_mobile.Clases.TicketItem
import com.example.vibe_mobile.ViewModel.TicketViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerViewTickets: RecyclerView
    private lateinit var adapter: TicketsAdapter
    private val ticketViewModel: TicketViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        showTickets(view)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTickets(view: View) {
        recyclerViewTickets = view.findViewById(R.id.user_tickets)
        recyclerViewTickets.layoutManager = LinearLayoutManager(requireContext())
        getTicketsUser()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getTicketsUser() {
        val apiService = RetrofitClient.createService(UserService::class.java)

        val userId = 3 /* Cambiar cuando tenga el log in */

            lifecycleScope.launch {
                try {
                    val response = apiService.getUserTickets(userId)

                    if (response.isSuccessful && response.body() != null) {
                        val userData = response.body()!!
                        val tickets = userData.tickets

                        val ticketItems = tickets.map { ticket ->
                            TicketItem(
                                id = ticket.id,
                                date = ticket.date,
                                time = ticket.time,
                                num_col = ticket.num_col,
                                num_row = ticket.num_row,
                                image = ticket.eventInfo.image,
                                title = ticket.eventInfo.title
                            )
                        }
                        adapter = TicketsAdapter(ticketItems)
                        recyclerViewTickets.adapter = adapter
                    } else {
                        Toast.makeText(requireContext(), "Error al cargar tickets", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}