package com.everon.everonmgr.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*


/**
 * <p><h3>Shared Preference util</h3></p>
 * Created by kmb on 5/24/2018.
 */
object PreferEx {
    //---------------------------------------------------
    // const Properties
    //---------------------------------------------------
    var PREFER_NAME = "DEFAULT_PREFER"
    var MODE = Context.MODE_PRIVATE

    //---------------------------------------------------
    // util
    //---------------------------------------------------
    var preferences: SharedPreferences? = null

    //---------------------------------------------------
    // private Properties
    //---------------------------------------------------

    //---------------------------------------------------
    // constructor
    //---------------------------------------------------
    fun initialize(context: Context){
        if(preferences==null){
            preferences = context.getSharedPreferences(PREFER_NAME, MODE)
        }
    }

    fun getPrefer(): SharedPreferences{
        if(preferences==null) throw Error("PreferEx not initialized")
        return preferences as SharedPreferences
    }

    fun clearAll(){
        getPrefer().edit().clear().apply()
    }

    //---------------------------------------------------
    // get / set
    //---------------------------------------------------

    //---------------------------------------------------
    // override, implement
    //---------------------------------------------------

    //---------------------------------------------------
    // public methods
    //---------------------------------------------------
    fun putString(key: String, value: String?) {
        val editor = getPrefer().edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = getPrefer().edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putFloat(key: String, value: Float) {
        val editor = getPrefer().edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun putInt(key: String, value: Int) {
        val editor = getPrefer().edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putLong(key: String, value: Long) {
        val editor = getPrefer().edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun putObj(key: String, obj: Any?) {
        val editor = getPrefer().edit()
        val str = if(obj==null) null else Gson().toJson(obj)
        editor.putString(key, str)
        editor.apply()
    }


    fun getString(key: String, defaultValue: String?): String? {
        return getPrefer().getString(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getPrefer().getBoolean(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return getPrefer().getFloat(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return getPrefer().getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return getPrefer().getLong(key, defaultValue)
    }

    fun <T> getObj(key: String, cls:Class<T>, defaultValue: T?): T? {
        val str = getPrefer().getString(key, null) ?: return defaultValue
        return Gson().fromJson(str, cls)
    }

    fun putStringSet(key: String, value: MutableSet<String>?) {
        val editor = getPrefer().edit()
        editor.putStringSet(key, value)
        editor.apply()
    }

    fun getStringSet(key: String, defaultValue: MutableSet<String>?): MutableSet<String>? {
        return getPrefer().getStringSet(key, defaultValue)
    }

    //---------------------------------------------------
    // list
    //---------------------------------------------------
    fun putIntList(key: String, value: List<Int>) {
        val gson = Gson()
        val list = ArrayList<Int>()
        list.addAll(value)
        val jsonText = gson.toJson(list)

        val editor = getPrefer().edit()
        editor.putString(key, jsonText)
        editor.apply()
    }

    fun getIntList(key: String, defaultValue: List<Int>): List<Int> {
        val gson = Gson()
        val jsonText:String = getPrefer().getString(key, null) ?: return defaultValue

        val arr = gson.fromJson(jsonText, Array<Int>::class.java)
        val result = ArrayList<Int>()
        result.addAll(Arrays.asList(*arr))

        return result
    }


}
