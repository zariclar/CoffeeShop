package com.example.coffeeshop.data.local

import android.content.Context

class SharedPrefsHelper(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("coffee_prefs", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        sharedPrefs.edit().putString("userId", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPrefs.getString("userId", null)
    }

    fun clearUser() {
        sharedPrefs.edit().clear().apply()
    }
}