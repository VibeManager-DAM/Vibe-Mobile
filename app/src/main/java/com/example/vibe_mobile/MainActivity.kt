package com.example.vibe_mobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        setContentView(R.layout.landing_page)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabSearch = findViewById<FloatingActionButton>(R.id.nav_search)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showToast("Home")
                R.id.nav_tickets -> showToast("Tickets")
                R.id.nav_chat -> showToast("Chat")
                R.id.nav_profile -> showToast("Perfil")
            }
            true
        }

        fabSearch.setOnClickListener {
            showToast("Buscar")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landingPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
