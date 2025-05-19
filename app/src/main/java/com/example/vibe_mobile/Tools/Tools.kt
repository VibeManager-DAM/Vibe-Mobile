package com.example.vibe_mobile.Tools

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.vibe_mobile.Clases.User
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

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

    fun logOut(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER).apply()
    }

    fun saveBitmapToTempFile(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}