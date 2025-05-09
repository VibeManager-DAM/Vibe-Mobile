package com.example.vibe_mobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    lateinit var navegation : BottomNavigationView

    private val mOnNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.itemFragmentHome -> {
                supportFragmentManager.commit {
                    replace<FirstFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemFragmentTickets -> {
                supportFragmentManager.commit {
                    replace<SecondFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemFragmentChat -> {
                supportFragmentManager.commit {
                    replace<ThirdFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemFragmentConfg -> {
                supportFragmentManager.commit {
                    replace<FourthFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navegation)

        navegation = findViewById(R.id.navMenu)
        navegation.setOnNavigationItemSelectedListener(mOnNavMenu)

        val ticketFragment = intent.getStringExtra("ticketFragment")
        val ticketTitle = intent.getStringExtra("ticketEventTitle")
        val ticketImage = intent.getStringExtra("ticketEventImage")

        // Set default fragment
        if (savedInstanceState == null) {
            when (ticketFragment) {
                "tickets" -> {
                    val bundle = Bundle().apply {
                        putString("ticketEventTitle", ticketTitle)
                        putString("ticketEventImage", ticketImage)
                    }
                    val secondFragment = SecondFragment().apply {
                        arguments = bundle
                    }
                    supportFragmentManager.commit {
                        replace(R.id.frameContainer, secondFragment)
                        setReorderingAllowed(true)
                        addToBackStack("replacement")
                    }
                    navegation.selectedItemId = R.id.itemFragmentTickets
                }
                "chat" -> navegation.selectedItemId = R.id.itemFragmentChat
                "config" -> navegation.selectedItemId = R.id.itemFragmentConfg
                else -> navegation.selectedItemId = R.id.itemFragmentHome
            }
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.navMenu)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0,0,0, systemBars.bottom)
            insets
        }
    }


}
