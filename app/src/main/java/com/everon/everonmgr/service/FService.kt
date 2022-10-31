package com.everon.everonmgr.service

import android.app.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.os.SystemClock
import com.everon.everonmgr.IAsyncListener
import com.everon.everonmgr.IEveronMgr
import com.everon.everonmgr.R
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.dto.FtpFileInfo
import com.everon.everonmgr.common.dto.createDummy
import com.everon.everonmgr.ctrl.HeartbeatCtrl
import com.everon.everonmgr.ctrl.InstallCtrl
import com.everon.everonmgr.net.ctrl.ApkCtrl
import com.everon.everonmgr.net.ctrl.LogCtrl
import com.everon.everonmgr.net.dto.Client
import com.everon.everonmgr.net.dto.RecentClient
import com.everon.everonmgr.util.LL
import com.everon.everonmgr.util.PreferEx
import com.everon.everonmgr.view.MainAct
import java.util.*


//import com.sample.sampleforegroundservice.MainActivity.Companion.ACTION_STOP_FOREGROUND

class FService : Service() {

  //---------------------------------------------------
  // example - https://proandroiddev.com/ipc-techniques-for-android-aidl-bb03ed62adaa
  //---------------------------------------------------
  companion object {
    var connectionCount: Int = 0
    val NOT_SENT = "Not sent!"
  }

  private val binder = object : IEveronMgr.Stub() {

    var listener: IAsyncListener? = null

    override fun getMessage(): String = "Message form mgr"

    override fun getPid(): Int = Process.myPid()

    override fun getConnectionCount(): Int = FService.connectionCount

    override fun setDisplayedValue(packageName: String?, pid: Int, data: String?) {
      val clientData =
        if (data == null || data.isEmpty()) NOT_SENT
        else data

      RecentClient.client = Client(
        packageName ?: NOT_SENT,
        pid.toString(),
        clientData,
        "AIDL"
      )

      LL.d("FService::setDisplayedValue() RecentClient.client: ${RecentClient.client}")
    }

    override fun addListener(listener: IAsyncListener?) {
      this.listener = listener
    }
    
    //---------------------------------------------------
    // public
    //---------------------------------------------------
    
  }

  override fun onBind(intent: Intent?): IBinder? {
    LL.d("FService::onBind() intent: $intent")
    connectionCount++
    return binder
    // return null
  }

  override fun onUnbind(intent: Intent?): Boolean {
    LL.d("FService::onUnbind() intent: $intent")
    RecentClient.client = null
    return super.onUnbind(intent)
  }
  
  //---------------------------------------------------
  // Foever
  //---------------------------------------------------
  val CHANNEL_DEFAULT_IMPORTANCE = "12121"
  val CHANNEL_ID = "channel12"
  val ONGOING_NOTIFICATION_ID = 1
  
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // init
    PreferEx.initialize(baseContext)

    // create noti
    createNotiChannel()

    LL.d("FService::onStartCommand() intent: $intent, flags: $flags, startId: $startId")
    // If the notification supports a direct reply action, use
    // PendingIntent.FLAG_MUTABLE instead.
    val notificationIntent = Intent(this, MainAct::class.java)
    val pendingIntent: PendingIntent
    pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      PendingIntent.getActivity(
        this,
        0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
      )
    } else {
      PendingIntent.getActivity(
        this,
        0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
      )
    }

    val notification: Notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
      .setContentTitle(getText(R.string.notification_title))
      .setContentText(getText(R.string.notification_message))
      .setSmallIcon(R.drawable.ic_launcher_background)
      .setContentIntent(pendingIntent)
      .setTicker(getText(R.string.ticker_text))
      .setChannelId(CHANNEL_ID)
      .build()

// Notification ID cannot be 0.
    startForeground(ONGOING_NOTIFICATION_ID, notification)

// 2
    start()

    return START_NOT_STICKY
  }

  override fun onDestroy() {
    LL.d("FService::onDestroy() ")
    super.onDestroy()
  }

  private fun createNotiChannel(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create the NotificationChannel
      val name = getString(R.string.channel_name)
      val descriptionText = getString(R.string.channel_description)
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
      mChannel.description = descriptionText
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(mChannel)
    }
  }

  private fun start(){
    LL.d("FService::startFun() ")

    // info
    chkSystemApp()

    // heartbeat
    HeartbeatCtrl.start(this)
    HeartbeatCtrl.stateListener = {
      LL.d("FService::start() HeartbeatCtrl.stateListener it: $it")
      when(it){
        HeartbeatCtrl.State.on_timer -> {
          LL.d("FService::start() binder?.listener: ${binder?.listener}")
          val timeStr = SystemClock.elapsedRealtime().toString()
          binder?.listener?.onResponse(timeStr)
        }
        HeartbeatCtrl.State.no_heartbeat -> {
          ApkCtrl.launchCharger(this)
        }
        else -> {

        }
      }
    }

    // install
    // heartbeat
    InstallCtrl.start(this)
    InstallCtrl.stateListener = {
      LL.d("FService::start() InstallCtrl.stateListener it: $it")
      when(it){
        else -> {

        }
      }
    }

    // start app on reboot
    ApkCtrl.launchCharger(this)

    // apk
//    downloadApk()
//    installApk()
//
    // log§
//    uploadLog()
//    uploadTest()
  }
  
  //---------------------------------------------------
  // launch app
  //---------------------------------------------------
  private fun toAppTest(){
    val intent: Intent = Uri.parse("https://www.android.com").let { webpage ->
      Intent(Intent.ACTION_VIEW, webpage)
    }
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

    LL.d("FService::toApp() intent: $intent")
    
    try {
      startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      // Define what your app should do if no activity can handle the intent.
      LL.d("FService::toApp() e: $e")
    }
  }

  //---------------------------------------------------
  // communication
  //---------------------------------------------------

  //---------------------------------------------------
  // apk
  //---------------------------------------------------
  private fun downloadApk(){
    var fileInfo = FtpFileInfo.createDummy()
//    fileInfo.file_name = "test"
    ApkCtrl.downloadApk(this, fileInfo, Config.DOWNLOAD_APK_MAX_RETRY_COUNT)
  }

  private fun installApk(){
    var fileInfo = FtpFileInfo.createDummy()
    ApkCtrl.installApk(this, fileInfo.file_name, Config.INSTALL_APK_MAX_RETRY_COUNT)
  }

  //---------------------------------------------------
  // log
  //---------------------------------------------------
  private fun uploadLog(){
    LogCtrl.uploadLog(this)
  }

  private fun uploadTest(){
    ApkCtrl.uploadTest(this)
  }

  //---------------------------------------------------
  // system app
  //---------------------------------------------------
  private fun chkSystemApp(){
    val ai = applicationInfo
    LL.d("FService::chkSystemApp() Application UID: ${ai.uid}, Process.SYSTEM_UID: ${Process.SYSTEM_UID}")
  }
  
}