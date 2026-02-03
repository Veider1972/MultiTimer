package ru.veider.multitimer.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

abstract class BasePreferences(
    context: Context
) {
    protected val prefs: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    fun putInt(key: String, value: Int) = prefs.edit { putInt(key, value) }

    fun getLong(key: String, default: Long): Long = prefs.getLong(key, default)
    fun putLong(key: String, value: Long) = prefs.edit { putLong(key, value) }

    fun getBool(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    fun putBool(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }

    fun getString(key: String): String? = prefs.getString(key, null)
    fun putString(key: String, value: String) = prefs.edit { putString(key, value) }
}