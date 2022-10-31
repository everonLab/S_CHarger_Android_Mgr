package com.everon.everonmgr.util

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import android.content.pm.PackageInstaller

import android.content.pm.PackageManager
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object PackageInstallEx {

  @Throws(IOException::class)
  fun installPackage(
    context: Context, installSessionId: String,
    packageName: String?,
    apkStream: InputStream
  ) {
    val packageManger: PackageManager = context.getPackageManager()
    val packageInstaller = packageManger.packageInstaller
    val params = android.content.pm.PackageInstaller.SessionParams(
      PackageInstaller.SessionParams.MODE_FULL_INSTALL
    )
    params.setAppPackageName(packageName)
    var session: android.content.pm.PackageInstaller.Session? = null
    try {
      val sessionId = packageInstaller.createSession(params)
      session = packageInstaller.openSession(sessionId)
      val out: OutputStream = session.openWrite(installSessionId, 0, -1)
      val buffer = ByteArray(1024)
      var length: Int
      var count = 0
      while (apkStream.read(buffer).also { length = it } != -1) {
        out.write(buffer, 0, length)
        count += length
        LL.d("PackageInstallEx::installPackage() count: $count")
      }
      session.fsync(out)
      out.close()

      LL.d("PackageInstallEx::installPackage() session: $session")

      val intent = Intent(Intent.ACTION_PACKAGE_ADDED)
      session.commit(
        PendingIntent.getBroadcast(
          context, sessionId,
          intent, PendingIntent.FLAG_UPDATE_CURRENT
        ).intentSender
      )
      LL.d("PackageInstallEx::installPackage() intent: $intent")

    } finally {
      session?.close()
    }
  }
}