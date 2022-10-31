package com.everon.everonmgr.sender

import android.content.Context
import android.content.Intent
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.IntentKey
import com.everon.everonmgr.util.LL

object Sender {
  //---------------------------------------------------
  // Heartbeat
  //---------------------------------------------------
  private var count = 0L

  fun sendHeartbeatResponse(context: Context){
    Intent().also { intent ->
//      intent.putExtra(IntentKey.heartbeat.ON_RESPONSE_HEARTBEAT, SystemClock.elapsedRealtime())
      intent.putExtra(IntentKey.heartbeat.ON_RESPONSE_HEARTBEAT, count++)
      sendMsg(context, intent, Config.MGR_APP_HEARTBEAT_ACTION)
    }
  }

  //---------------------------------------------------
  // Install
  //---------------------------------------------------
  fun sendUpdateDownloadApk(
    context: Context, received: Long, fileSize: Long
  ) {
    Intent().also { intent ->
      val progress = longArrayOf(received, fileSize)
      intent.putExtra(IntentKey.install.ON_UPDATE_DOWNLOAD_APK, progress)
      sendMsg(context, intent, Config.MGR_APP_INSTALL_ACTION)
    }
  }

  fun sendCompleteDownloadApk(
    context: Context, success: Boolean, msg: String?
  ) {
    Intent().also { intent ->
      if (success){
        intent.putExtra(IntentKey.install.ON_SUCCESS_DOWNLOAD_APK, true)
      }else{
        intent.putExtra(IntentKey.install.ON_FAILURE_DOWNLOAD_APK, msg)
      }
      sendMsg(context, intent, Config.MGR_APP_INSTALL_ACTION)
    }
  }

  fun sendFailInstallApk(
    context: Context, msg: String?
  ) {
    Intent().also { intent ->
      intent.putExtra(IntentKey.install.ON_FAILURE_INSTALL_APK, msg)
      sendMsg(context, intent, Config.MGR_APP_INSTALL_ACTION)
    }
  }

  //---------------------------------------------------
  // util
  //---------------------------------------------------
  private fun sendMsg(
    context: Context, intent: Intent, action: String
  ){
    intent.action = action
    LL.d("Sender::sendMsg() intent: ${intent.toUri(0)}")
    context.sendBroadcast(intent)
  }
}