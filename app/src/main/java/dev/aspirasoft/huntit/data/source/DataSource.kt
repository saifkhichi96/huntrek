package dev.aspirasoft.huntit.data.source

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

/**
 * Created by saifkhichi96 on 23/12/2017.
 */
object DataSource {

    private const val DATABASE_NAME = "HuntIt.db"

    val remoteDb = FirebaseDatabase.getInstance().reference
    lateinit var localDb: SharedPreferences

    private var MAX_CHEST: Int = 0

    fun init(context: Context) {
        localDb = context.getSharedPreferences(DATABASE_NAME, MODE_PRIVATE)
        MAX_CHEST = localDb.getString("MAX_CHEST", null)?.toInt() ?: 0
    }

    fun <T> get(key: String, type: Class<T>, default: T? = null): T? {
        return try {
            val json = localDb.getString(key, null)
            if (json == null) default else Gson().fromJson(json, type)
        } catch (ex: Exception) {
            default
        }
    }

    fun <T> get(key: String, default: T): T {
        return try {
            val v = localDb.all[key]
            if (v == null) default else v as T
        } catch (ex: Exception) {
            default
        }
    }

    fun put(key: String, value: Any?) {
        when (value) {
            null -> remove(key)
            else -> localDb.edit().putString(key, Gson().toJson(value)).apply()
        }
    }

    fun remove(key: String) {
        try {
            localDb.edit().remove(key).apply()
        } catch (_: Exception) {

        }
    }

}