package com.everon.everonmgr.util

import android.util.Log

object LL {
  //---------------------------------------------------
  // const Properties
  //---------------------------------------------------
  /** 20.9.8 var 로 변경
  private const val USE_LOG = true
   */
  var USE_LOG = true
  private const val TAG = "_root"

  //---------------------------------------------------
  // private Properties
  //---------------------------------------------------

  //---------------------------------------------------
  // get / set
  //---------------------------------------------------

  //---------------------------------------------------
  // constructor
  //---------------------------------------------------

  //---------------------------------------------------
  // override, implement
  //---------------------------------------------------

  //---------------------------------------------------
  // public methods
  //---------------------------------------------------
  fun d(msg: String) {
    if (!USE_LOG) return
    Log.d(TAG, msg)
  }

  fun d(msg: String, obj: Any?) {
    if (!USE_LOG) return

    if (obj is List<*>) {
      dList(msg, obj as List<*>?)
      return
    } else if (obj is Array<*>) {
      dList(msg, obj)
      return
    }

    Log.d(TAG, msg + (obj?.toString() ?: "null"))
  }

  fun d(msg: String, str: String) {
    if (!USE_LOG) return
    Log.d(TAG, msg + str)
  }

  private fun dList(msg: String, list: Array<*>) {
    if (!USE_LOG) return
    val sb = StringBuilder("[")
    for (e in list) {
      sb.append("\n")
      sb.append("\t").append(e.toString())
    }
    sb.append("\n")
    sb.append("]")
    Log.d(TAG, msg + sb.toString())
  }

  private fun dList(msg: String, list: List<*>?) {
    if (!USE_LOG) return
    if (list == null) {
      Log.d(TAG, "$msg list is null")
      return
    }

    val sb = StringBuilder("[")
    for (e in list) {
      sb.append("\n")
      if (e != null) sb.append("\t").append(e.toString())
    }
    sb.append("\n")
    sb.append("]")
    Log.d(TAG, msg + sb.toString())
  }

  fun dListAll(msg: String, list: List<*>?) {
    if (!USE_LOG) return
    if (list == null) {
      Log.d(TAG, "$msg list is null")
      return
    }

    Log.d(TAG, "$msg[")
    for (e in list) {
      Log.d(TAG, msg + "\t" + e.toString())
    }
    Log.d(TAG, "$msg]")
  }

  fun d(msg: String, tr: Throwable) {
    if (!USE_LOG) return
    Log.d(TAG, msg, tr)
  }

  fun e(msg: String) {
    if (!USE_LOG) return
    Log.e(TAG, msg)
  }

  fun e(msg: String, tr: Throwable) {
    if (!USE_LOG) return
    Log.e(TAG, msg, tr)
  }

  fun i(msg: String) {
    if (!USE_LOG) return
    Log.i(TAG, msg)
  }

  fun i(msg: String, tr: Throwable) {
    if (!USE_LOG) return
    Log.i(TAG, msg, tr)
  }

  fun v(msg: String) {
    if (!USE_LOG) return
    Log.v(TAG, msg)
  }

  fun v(msg: String, tr: Throwable) {
    if (!USE_LOG) return
    Log.v(TAG, msg, tr)
  }

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------
}