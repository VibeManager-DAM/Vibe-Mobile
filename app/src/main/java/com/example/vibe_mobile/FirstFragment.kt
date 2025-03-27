package com.example.vibe_mobile

import CardAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Clases.CardItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Creamos una variable view para poder modificar la vista y creamos funciones
        // para mostrarlas.
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        showRV(view)

        return view
    }

    private fun showRV(view: View) {
        val cardList = listOf(
            CardItem("Concierto en Mérida", "18 May", "€150.00", R.drawable.evento_image, 100, true, "Etiam et diam quis sem ultrices ultrices. Curabitur a metus ac erat consectetur consectetur quis eu nisi."),
            CardItem("Concierto en Por ahí", "09 Sept", "€10.00", R.drawable.evento_image,  200, false, "Etiam et diam quis sem ultrices ultrices.erat consectetur consectetur quis eu nisi."),
            CardItem("Concierto en Juancho", "31 Mar", "€70.00", R.drawable.evento_image,  312, true, "Curabitur a metus ac erat consectetur consectetur quis eu nisi."),
            // más ítems si querés
        )

        recyclerView = view.findViewById(R.id.recyclerViewCard)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CardAdapter(cardList)

        recyclerView.adapter = adapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}