package com.example.vibe_mobile.Tools

import android.content.Context
import com.example.vibe_mobile.Clases.User
import com.google.gson.Gson

object Tools {

    private const val PREFS_NAME = "vibe_prefs"
    private const val KEY_USER = "logged_user"

    fun saveUser(context: Context, user: User?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val userJson = Gson().toJson(user)
        editor.putString(KEY_USER, userJson)
        editor.apply()
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) Gson().fromJson(userJson, User::class.java) else null
    }

    fun clearUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER).apply()
    }
}