package com.everon.everonmgr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BasicReceiver: BroadcastReceiver() {

  var onReceiveListener: ((intent: Intent?) -> Unit)? = null

  override fun onReceive(context: Context?, intent: Intent?) {
//    val heartbeat = intent?.extras?.get("heartbeat")
//    LL.d("HeartbeatReceiver::onReceive() intent: $intent, heartbeat: $heartbeat")
    onReceiveListener?.let { it(intent) }
  }
}