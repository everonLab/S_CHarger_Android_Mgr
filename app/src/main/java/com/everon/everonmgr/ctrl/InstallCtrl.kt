package com.everon.everonmgr.ctrl

import android.content.Context
import android.content.IntentFilter
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.IntentKey
import com.everon.everonmgr.common.PreferKey
import com.everon.everonmgr.common.dto.FtpFileInfo
import com.everon.everonmgr.common.dto.createFileNameOnlyDummy
import com.everon.everonmgr.common.extension.GsonFac
import com.everon.everonmgr.common.listener.FileDownloadListener
import com.everon.everonmgr.net.ctrl.ApkCtrl
import com.everon.everonmgr.receiver.BasicReceiver
import com.everon.everonmgr.sender.Sender
import com.everon.everonmgr.util.LL
import com.everon.everonmgr.util.PreferEx
import java.io.File

interface InstallListener {
  fun onComplete(success: Boolean, file: File, msg: String?)
}

object InstallCtrl {
  //---------------------------------------------------
  // const Properties
  //---------------------------------------------------

  //---------------------------------------------------
  // public Properties
  //---------------------------------------------------
  // state
  enum class State{
    start, stop,
    download_apk,
    install_apk,
    on_complete_install_apk,
    launch_apk,
    on_complete_launch_apk
  }
  var state: State = State.start
  var stateListener: ((state: State) -> Unit)? = null

  //---------------------------------------------------
  // private Properties
  //---------------------------------------------------
  // receiver
  private var receiver: BasicReceiver? = null

  //---------------------------------------------------
  // public methods
  //---------------------------------------------------
  fun start(context: Context) {
    changeState(context, State.start)
  }

  fun stop(context: Context){
    changeState(context, State.stop)
  }

  fun downloadApk(context: Context, ftpFileInfo: FtpFileInfo, maxRetryCount: Int) {
    changeState(context, State.download_apk, ftpFileInfo, maxRetryCount)
  }

  fun installApk(context: Context){
    changeState(context, State.install_apk)
  }

  fun isInatallOrLaunchState(): Boolean{
    LL.d("InstallCtrl::isInatallOrLaunchState() state: $state")
    return when(state){
      State.install_apk, State.launch_apk -> {
        true
      }
      else -> {
        false
      }
    }
  }

  //---------------------------------------------------
  // state
  //---------------------------------------------------
  private fun changeState(context: Context, state: State, vararg values: Any?){
    LL.d("InstallCtrl::changeState() state: $state, values: ", values)
    this.state = state

    when(state){
      State.start ->{
        registerReceiver(context)
      }

      State.stop ->{
        unRegisterReceiver(context)
      }

      State.download_apk ->{
        val apkFileInfo = values.getOrNull(0) as? FtpFileInfo
        val maxRetryCount = values.getOrNull(1) as? Int
        apkFileInfo?.let {
          ApkCtrl.downloadApk(context, it, maxRetryCount, (object: FileDownloadListener{
            override fun onReceived(received: Long, fileSize: Long) {
              Sender.sendUpdateDownloadApk(context, received, fileSize)
            }
            override fun onComplete(success: Boolean, file: File?, message: String?) {
              LL.d("InstallCtrl::onComplete() success: $success, file: ${file?.name}, message: $message")
              Sender.sendCompleteDownloadApk(context, success, message)
            }
          }))
        }
      }

      State.install_apk ->{
        val apkFileInfo = values.getOrNull(0) as? FtpFileInfo
        val maxRetryCount = values.getOrNull(1) as? Int
        apkFileInfo?.let {
          ApkCtrl.installApk(context, it.file_name, maxRetryCount, (object : InstallListener {
            override fun onComplete(success: Boolean, file: File, msg: String?) {
              changeState(context, State.on_complete_install_apk, success, file, msg)
            }
          }))
        } ?: run {
          changeState(context, State.start)
        }
      }

      State.on_complete_install_apk ->{
        // history
        val success = values.getOrNull(0) as? Boolean
        val file = values.getOrNull(1) as? File
        val msg = values.getOrNull(2) as? String
        if (success != null && file != null) {
          // success - launch
          if (success){
            changeState(context, State.launch_apk, file)
          }
          // fail - send fail msg
          else{
            Sender.sendFailInstallApk(context, msg)
          }
        }
      }

      State.launch_apk ->{
        val file = values.getOrNull(0) as? File
        file?.let {
          ApkCtrl.launchChargerAfterInstall(context, it.name)
        }

        // 일단 start 로 돌아간 후 on_complete_launch_apk 기다림
        changeState(context, State.start)
      }

      State.on_complete_launch_apk ->{
        // history
        val success = values.getOrNull(0) as? Boolean
        val apkName = values.getOrNull(1) as? String
        if (success != null && apkName != null) {
          onComplteLaunch(context, success, apkName)
        }
      }

      else -> {}
    }
    stateListener?.invoke(state)
  }

  //---------------------------------------------------
  // receiver
  //---------------------------------------------------
  private fun createReceiver(context: Context){
    receiver = BasicReceiver()
    receiver?.onReceiveListener = { it ->
      LL.d("InstallCtrl::createReceiver() it: $it")
      it?.let { intent ->

        // DOWNLOAD_APK
        val jsonStr0 = intent.extras?.getSerializable(IntentKey.install.DOWNLOAD_APK) as? String
        jsonStr0?.let { jsonStr ->
          val downloadFtpFileInfo: FtpFileInfo = GsonFac.createBasic().fromJson(jsonStr, FtpFileInfo::class.java)
          val maxRetryCount = intent.getIntExtra(IntentKey.install.MAX_RETRY_COUNT, 0)
          downloadFtpFileInfo?.let { ftpFileInfo ->
            LL.d("InstallCtrl::createReceiver() DOWNLOAD_APK ftpFileInfo: $ftpFileInfo, maxRetryCount: $maxRetryCount")
            changeState(context, State.download_apk, ftpFileInfo, maxRetryCount)
          }
        }

        // INSTALL_APK
        val jsonStr1 = intent.extras?.getSerializable(IntentKey.install.INSTALL_APK) as? String
        jsonStr1?.let { jsonStr ->
          val installFtpFileInfo: FtpFileInfo = GsonFac.createBasic().fromJson(jsonStr, FtpFileInfo::class.java)
          val maxRetryCount = intent.getIntExtra(IntentKey.install.MAX_RETRY_COUNT, 0)
          installFtpFileInfo?.let { ftpFileInfo ->
            LL.d("InstallCtrl::createReceiver() INSTALL_APK ftpFileInfo: $ftpFileInfo, maxRetryCount: $maxRetryCount")
            changeState(context, State.install_apk, ftpFileInfo, maxRetryCount)
          }
        }

        // LAUNCH_APK
        intent.extras?.getString(IntentKey.install.ON_SUCCESS_LAUNCH_APK)?.let { apkName ->
          LL.d("InstallCtrl::createReceiver() ON_SUCCESS_LAUNCH_APK apkName: $apkName")
          changeState(context, State.on_complete_launch_apk, true, apkName)
        }
        intent.extras?.getString(IntentKey.install.ON_FAILURE_LAUNCH_APK)?.let { apkName ->
          LL.d("InstallCtrl::createReceiver() ON_FAILURE_LAUNCH_APK apkName: $apkName")
          changeState(context, State.on_complete_launch_apk, false, apkName)
        }
      }
    }
  }

  private fun registerReceiver(context: Context){
    if (receiver != null) unRegisterReceiver(context)
    if (receiver == null) createReceiver(context)
    context.registerReceiver(receiver, IntentFilter(Config.CHARGE_APP_INSTALL_ACTION))
  }

  private fun unRegisterReceiver(context: Context){
    try {
      context.unregisterReceiver(receiver)
    }catch (e: Exception){
      LL.d("InstallCtrl::unRegisterReceiver() e: $e")
    }
  }
  
  //---------------------------------------------------
  // launch
  //---------------------------------------------------
  private fun onComplteLaunch(context: Context, success: Boolean, apkName: String){
    val lastSuccessApkName = PreferEx.getString(PreferKey.LAST_LAUNCH_SUCCESS_APK_NAME, null)

    // ----------------------- success
    if (success){
      val currentSuccessApkName = apkName

      // same with old success
      if (currentSuccessApkName.equals(lastSuccessApkName)){

      }
      // not same with old
      else{
        // delete old success file
        lastSuccessApkName?.let {
          ApkCtrl.deleteApk(context, it)
        }
      }

      // update last success
      PreferEx.putString(PreferKey.LAST_LAUNCH_SUCCESS_APK_NAME, currentSuccessApkName)
    }
    // ----------------------- fail
    else{
      // install - last success apk TODO - need prevent loop ?
      lastSuccessApkName?.let {
        val ftpFileInfo = FtpFileInfo.createFileNameOnlyDummy(it)
        changeState(context, State.install_apk, ftpFileInfo, 1)
      }
    }
  }

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------

}