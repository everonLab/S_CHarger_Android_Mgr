package com.everon.everonmgr.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.everon.everonmgr.service.FService
import java.util.*
import kotlin.concurrent.timerTask
import com.everon.everonmgr.R.*

class MainAct: Activity() {
  //---------------------------------------------------
  // const Properties
  //---------------------------------------------------

  //---------------------------------------------------
  // private Properties
  //---------------------------------------------------

  //---------------------------------------------------
  // override, implement
  //---------------------------------------------------
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_main)

    val intent = Intent(this, FService::class.java)
    applicationContext.startForegroundService(intent)
//    startPermissionActivity()

    // systemId 가 아닌 일반 설치시 퍼미션 필요 (에뮬 테스트등 환경)
//    startPermissionActivity()

    // systemId 로 설치시 - auto close
    Timer().schedule(timerTask {
      finish()
    }, 4000L)
  }

  //---------------------------------------------------
  // public methods
  //---------------------------------------------------

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------
}

fun Context.drawOverOtherAppsEnabled(): Boolean {
  return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
    true
  } else {
    Settings.canDrawOverlays(this)
  }
}


fun Context.startPermissionActivity() {
  startActivity(
    Intent(this, PermAct::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
  )
}