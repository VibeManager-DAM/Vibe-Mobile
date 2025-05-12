package com.example.vibe_mobile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.example.vibe_mobile.API.RetrofitClient
import com.example.vibe_mobile.API.Users.UserRepository
import com.example.vibe_mobile.API.Users.UserService
import com.example.vibe_mobile.Activities.IniciarSesionActivity
import com.example.vibe_mobile.Clases.User
import com.example.vibe_mobile.Clases.UserUpdateRequest
import com.example.vibe_mobile.Tools.Tools
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FourthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FourthFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_fourth, container, false)
        val confButton = view.findViewById<AppCompatButton>(R.id.conf_options)
        confButton.setOnClickListener {
            showPopupMenu(it)
        }
        showUserInfo(view)
        modifyUser(view)
        return view
    }

    private fun modifyUser(view: View) {
        val btnModifyUser: AppCompatButton = view.findViewById(R.id.btn_saveChanges)

        btnModifyUser.setOnClickListener {
            val userName = view.findViewById<EditText>(R.id.user_nombre).text.toString()
            val userMail = view.findViewById<EditText>(R.id.user_correo).text.toString()
            val userPassword = view.findViewById<EditText>(R.id.user_contrasena).text.toString()
            val user = Tools.getUser(requireContext())?.copy(
                fullname = userName,
                email = userMail,
                password = userPassword
            )

            if (user != null) {
                updateUser(user)
            } else {
                Toast.makeText(context, "Usuario no válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser(user: User) {
        val userService = RetrofitClient.createService(UserService::class.java)
        val updateRequest = UserUpdateRequest(
            fullname = user.fullname,
            email = user.email,
            password = user.password
        )

        lifecycleScope.launch {
            try {
                val response = userService.modifyUser(user.id!!, updateRequest)

                if (response.isSuccessful && response.body()?.success == true) {
                    val usuarioActualizado = response.body()?.user
                    Toast.makeText(requireContext(), "Usuario actualizado: ${usuarioActualizado?.fullname}", Toast.LENGTH_SHORT).show()
                } else {
                    val msg = response.body()?.message ?: "Error al actualizar"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Fallo de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUserInfo(view: View) {
        val userName = view.findViewById<EditText>(R.id.user_nombre)
        val userMail = view.findViewById<EditText>(R.id.user_correo)
        val userPassword = view.findViewById<EditText>(R.id.user_contrasena)
        val user = Tools.getUser(requireContext()) ?: return

        userName.setText(user.fullname)
        userMail.setText(user.email)
        userPassword.setText(user.password)
    }

    private fun showPopupMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_configuracion, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

                    Tools.logOut(requireContext())
                    val intent = Intent(requireContext(), IniciarSesionActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FourthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FourthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}