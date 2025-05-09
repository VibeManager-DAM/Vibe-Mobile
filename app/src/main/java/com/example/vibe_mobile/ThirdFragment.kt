package com.example.vibe_mobile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vibe_mobile.Activities.InsideChatActivity
import com.example.vibe_mobile.Adapters.ChatPreviewAdapter
import com.example.vibe_mobile.Tools.Tools
import com.example.vibe_mobile.API.Users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showChats()
    }

    private fun showChats() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerChats) ?: return
        val user = Tools.getUser(requireContext()) ?: return
        val userId = user.id ?: return

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    UserRepository().getChatsByUserId(userId)
                }

                if (response.isSuccessful) {
                    val chatList = response.body() ?: emptyList()

                    val adapter = ChatPreviewAdapter(chatList) { chat ->
                        val intent = Intent(requireContext(), InsideChatActivity::class.java)
                        intent.putExtra("chat_id", chat.ChatId)
                        intent.putExtra("event_title", chat.EventTitle)
                        intent.putExtra("event_image", chat.image)
                        startActivity(intent)
                    }

                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error cargando chats", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}