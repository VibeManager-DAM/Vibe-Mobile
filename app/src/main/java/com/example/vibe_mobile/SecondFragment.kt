package com.example.vibe_mobile

import TicketsAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Clases.TicketItem
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
        val tickets = listOf(
            TicketItem(
                id = 1,
                title = "Concierto de Jazz",
                date = LocalDate.of(2025, 5, 18),
                time = LocalTime.of(20, 0),
                numCol = 5,
                numRow = 2,
                imageResId = R.drawable.evento_image
            ),
            TicketItem(
                id = 2,
                title = "Festival Indie",
                date = LocalDate.of(2025, 5, 21),
                time = LocalTime.of(21, 0),
                numCol = 3,
                numRow = 4,
                imageResId = R.drawable.evento_image
            ),
            TicketItem(
                id = 3,
                title = "Obra de Teatro Clásico",
                date = LocalDate.of(2025, 5, 29),
                time = LocalTime.of(19, 30),
                numCol = 2,
                numRow = 1,
                imageResId = R.drawable.evento_image
            )
        )

        recyclerViewTickets = view.findViewById(R.id.recyclerTickets)
        recyclerViewTickets.layoutManager = LinearLayoutManager(requireContext())
        adapter = TicketsAdapter(tickets)
        recyclerViewTickets.adapter = adapter
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