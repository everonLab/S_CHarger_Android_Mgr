package com.everon.everonmgr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.everon.everonmgr.service.FService

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    if (Intent.ACTION_BOOT_COMPLETED == intent!!.action) {
//      val i = Intent(context, MainAct::class.java)
//      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//      context!!.startActivity(i)

      val serviceIntent = Intent(context, FService::class.java)
      context?.startForegroundService(serviceIntent)
    }
  }
}