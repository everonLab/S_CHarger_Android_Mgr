package com.everon.everonmgr.net.ctrl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.IntentKey
import com.everon.everonmgr.common.dto.FtpFileInfo
import com.everon.everonmgr.common.listener.FileDownloadListener
import com.everon.everonmgr.ctrl.InstallListener
import com.everon.everonmgr.net.Api
import com.everon.everonmgr.net.Ftp
import com.everon.everonmgr.net.util.FileUtil
import com.everon.everonmgr.util.LL
import com.everon.everonmgr.util.PackageInstallEx
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*
import kotlin.concurrent.timerTask


object ApkCtrl {
  //---------------------------------------------------
  // ftp
  //---------------------------------------------------
  // ftp - apk: EVSAF122.apk
  fun downloadApk(
    context: Context,
    ftpFileInfo: FtpFileInfo,
    maxRetryCount: Int? = 0,
    fileDownloadListener: FileDownloadListener? = null
  ){
//    val file = File(context.cacheDir, ftpFileInfo.file_name)
    val file = getApkFile(context, ftpFileInfo.file_name)
    Ftp.everon.download(ftpFileInfo, file, maxRetryCount ?: 0, fileDownloadListener)
  }

  fun uploadTest(context: Context){
    val file = File(context.cacheDir, "t1.txt")
    file.printWriter().use { out ->
      out.println("ftp test -------------")
    }
//    LL.d("ApkCtrl::uploadTest() file.readText(): ${file.readText()}")
    /**
    Ftp.everon.upload(AppConfig.ftpApkPath, file)
    */
  }

  //---------------------------------------------------
  // apk
  //---------------------------------------------------
  private var installRetryCount: Int = 0

  fun installApk(
    context: Context,
    apkName: String,
    maxRetryCount: Int? = 0,
    installListener: InstallListener? = null
  ){
    LL.d("ApkCtrl::installApk() ----------------------------- START")
//    val apkFile = File(context.cacheDir, apkName)
    val apkFile = getApkFile(context, apkName)

//    val apkUri = FileUtil.uriFromFile(context, apkFile)
//    LL.d("ApkCtrl::installApk() apkFile.exists: ${apkFile.exists()}, apkUri: $apkUri")

//    apkUri?.let { packageInstall(context, it, apkFile) }
//    addApkToInstallSession(context, apkFile)
//    _installAPK_old(context, apkFile)

    // init
    installRetryCount = 0

    // install
    _installApk_Everon(context, apkFile, maxRetryCount ?: 0, installListener)
  }

  fun deleteApk(context: Context, apkName: String){
    val file = getApkFile(context, apkName)
    if (file.exists()) {
      file.delete()
    }
  }

  private fun packageInstall(context: Context, uri: Uri, file: File) {
    LL.d("ApkCtrl::packageInstall() 1")


//    LL.d("ApkCtrl::packageInstall() FileInputStream(file): ${FileInputStream(file)}")
//    var inputStream: FileInputStream = FileInputStream(file)
//    inputStream.close()

    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

    LL.d("ApkCtrl::packageInstall() 2 inputStream: $inputStream")
    inputStream?.let {
      PackageInstallEx.installPackage(context, "everontestapp", packageName = "com.example.everontestapp", apkStream = it)
    }
  }

  private fun addApkToInstallSession(context: Context, file: File) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val uri = FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName.toString() + ".provider",
        file
      )
      LL.d("ApkCtrl::addApkToInstallSession() uri: $uri")

      val intent = Intent(Intent.ACTION_VIEW)
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.setDataAndType(uri, "application/vnd.android.package-archive")
      context.startActivity(intent)
    }
  }

  private fun _installAPK_old(context: Context, file: File) {
//    val PATH = Environment.getExternalStorageDirectory().toString() + "/" + "apkname.apk"
//    val file = File(PATH)
    if (file.exists()) {
      val intent = Intent(Intent.ACTION_VIEW)
      val uri = FileUtil.uriFromFile(context, file)
      LL.d("ApkCtrl::_installAPK() uri: $uri")
      intent.setDataAndType(
        uri, "application/vnd.android.package-archive"
      )

      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      try {
        context.startActivity(intent)
      } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        LL.e("ApkCtrl::_installAPK: Error in opening the file! e: $e")
      }
    } else {
      Toast.makeText(
        context,
        "installing",
        Toast.LENGTH_LONG
      ).show()
    }
  }

  private fun perm(context: Context){
    //installtion permission

    //installtion permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (context.packageManager.canRequestPackageInstalls()) {
        context.startActivity(
          Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
            Uri.parse(
              java.lang.String.format("package:%s", context.packageName)
            )
          )
        )
      } else {
      }
    }

    //Storage Permission
  }

  //---------------------------------------------------
  // test
  //---------------------------------------------------
  private fun createTxt(context: Context){
    File(context.applicationContext.getExternalFilesDir(null), "test2.txt").printWriter().use { out ->
      out.println("gogogogogogogogo NOW")
    }

//    context.applicationContext.openFileOutput("test2.txt", Context.MODE_PRIVATE).use {
//      it.write("gogogogogogogogo NOW".toByteArray())
//    }
  }

  private fun readTextFromUri(context: Context, uri: Uri): String {
    val stringBuilder = StringBuilder()
    LL.d("ApkCtrl::readTextFromUri() uri: $uri")
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
      LL.d("ApkCtrl::readTextFromUri() inputStream: $inputStream")
      BufferedReader(InputStreamReader(inputStream)).use { reader ->
        var line: String? = reader.readLine()
        while (line != null) {
          stringBuilder.append(line)
          line = reader.readLine()
        }
      }
    }
    return stringBuilder.toString()
  }


  //---------------------------------------------------
  // file 
  //---------------------------------------------------
  private fun getApkFile(context: Context, fileName: String): File{
//    return File(context.cacheDir, fileName)
    return File(Config.APK_DOWNLOAD_PATH, fileName)
  }

  private fun writeResponseBodyToDisk(context: Context, body: ResponseBody, fileName: String): Boolean {
    return try {
      // todo change the file location/name according to your needs

      val futureStudioIconFile =
        File(context.getExternalFilesDir(null), fileName )

      LL.d("ApkCtrl::writeResponseBodyToDisk() futureStudioIconFile: ${futureStudioIconFile.absolutePath}")
      if (futureStudioIconFile.exists()){
        futureStudioIconFile.delete()
      }

      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      try {
        val fileReader = ByteArray(4096)
        val fileSize = body.contentLength()
        var fileSizeDownloaded: Long = 0
        inputStream = body.byteStream()
        outputStream = FileOutputStream(futureStudioIconFile)
        while (true) {
          val read: Int = inputStream.read(fileReader)
          if (read == -1) {
            break
          }
          outputStream.write(fileReader, 0, read)
          fileSizeDownloaded += read.toLong()
          LL.d("ApkCtrl::writeResponseBodyToDisk() fileSizeDownloaded: $fileSizeDownloaded  of $fileSize")
        }
        outputStream.flush()
        true
      } catch (e: IOException) {
        false
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
        if (outputStream != null) {
          outputStream.close()
        }
      }
    } catch (e: IOException) {
      false
    }
  }

  //---------------------------------------------------
  // everon source
  //---------------------------------------------------
  private fun unintsallPackage_Everon() {
    try {
      val log = arrayOfNulls<java.lang.StringBuilder>(1)
      val process = Runtime.getRuntime().exec("suc")
      val os = DataOutputStream(process.outputStream)
      //com.speel.SerialTester
      val strUninstallCmd = "pm uninstall ${Config.CHARGE_APP_ID}\n"
      os.writeBytes(strUninstallCmd)
      os.flush()
      os.close()
      val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
      // Grab the results
      log[0] = java.lang.StringBuilder()
      var line: String
      while (bufferedReader.readLine().also { line = it } != null) {
        log[0]!!.append(
          """
                $line
                
                """.trimIndent()
        )
      }
      process.waitFor()
      LL.d("@combatspirit", "install finished - system reboot")
    } catch (iex: InterruptedIOException) {
      iex.printStackTrace()
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
      LL.d("@combatspirit", ex.message)
    }
  }

  private fun _installApk_Everon(
    context: Context,
    file: File,
    maxRetryCount: Int = 0,
    installListener: InstallListener? = null) {
    try {
//      val strPath: String = m_strPath // path , filename 분리 해서 진행..!!
      var strPath: String = file.absolutePath
      LL.d("ApkCtrl::installApk_Everon()----------- file.exist(): ${file.exists()}, strPath: $strPath")

      val log = arrayOfNulls<java.lang.StringBuilder>(1)

      try {
        // Run the command
        val arrayCommand = arrayOf("suc", "-c", "pm install -r $strPath")

        val r = Runtime.getRuntime()
        val process = r.exec(arrayCommand)

//        val process = Runtime.getRuntime().exec("sh", "-c", "echo $BOOTCLASSPATH");

        val stdoutString = convertInputStreamToString(process.inputStream)
        val stderrString = convertInputStreamToString(process.errorStream)

        LL.d("ApkCtrl::installApk_Everon() stdoutString: $stdoutString")
        LL.d("ApkCtrl::installApk_Everon() stderrString: $stderrString")

        LL.d("ApkCtrl::installApk_Everon() log: ${log.toString()}")

        val isSuccess: Boolean = stderrString?.isEmpty() ?: false
        val msg = if (isSuccess) stdoutString else stderrString

//        installListener?.onComplete(isSuccess, file, msg)
        _onComplete_installApk_Everon(context, isSuccess, file, maxRetryCount, installListener, msg)

      } catch (e: IOException) {
        LL.d("ApkCtrl::installApk_Everon() e: $e")
//        installListener?.onComplete(false, file, e.message)
        _onComplete_installApk_Everon(context, false, file, maxRetryCount, installListener, e.message)
      }

      LL.d("@combatspirit", "install finished - system reboot")
    } catch (iex: InterruptedIOException) {
      iex.printStackTrace()
//      installListener?.onComplete(false, file, iex.message)
      _onComplete_installApk_Everon(context, false, file, maxRetryCount, installListener, iex.message)
    } catch (ex: java.lang.Exception) {
      ex.printStackTrace()
      LL.d("@combatspirit", ex.message)
//      installListener?.onComplete(false, file, ex.message)
      _onComplete_installApk_Everon(context, false, file, maxRetryCount, installListener, ex.message)
    }
  }

  private fun _onComplete_installApk_Everon(
    context: Context,
    success: Boolean,
    file: File,
    maxRetryCount: Int = 0,
    installListener: InstallListener? = null,
    msg: String? = ""
    ){
    // ----------------------- success
    if (success){
      // listener
      installListener?.onComplete(success, file, msg)
    }
    // ----------------------- fail
    else{
      // retry
      if (installRetryCount < maxRetryCount){
        installRetryCount++
        // delay
        Timer().schedule(timerTask {
          _installApk_Everon(context, file, maxRetryCount, installListener)
        }, Config.INSTALL_APK_RETRY_DELAY)
      }
      // retry over
      else{
        installListener?.onComplete(success, file, msg)
        LL.d("ApkCtrl::_onComplete_installApk_Everon() RETRY OVER msg: $msg")
      }
    }
  }

  fun launchChargerAfterInstall(context: Context, fileName: String) {
    // delay - 기존앱에 존재
    Thread.sleep(Config.LAUNCH_APP_DELAY)

    //com.speel.SerialTester
    val intent: Intent? = context.packageManager.getLaunchIntentForPackage(Config.CHARGE_APP_ID)
//    val intent: Intent? = context.packageManager.getLaunchIntentForPackage("com.example.everontestapp")

    LL.d("ApkCtrl::launchChargerAfterInstall() intent: $intent")
    intent?.let{
      it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      it.putExtra(IntentKey.install.LAUNCH_APK, fileName)
      context.startActivity(it)
    }
  }

  fun launchCharger(context: Context){
    val intent = context.packageManager.getLaunchIntentForPackage(Config.CHARGE_APP_ID)
    LL.d("ApkCtrl::launchCharger() intent: $intent")
    intent?.let {
      it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(it)
    }
  }


  //---------------------------------------------------
  // util
  //---------------------------------------------------
  // "/data/local/tmp/"
  private fun copyFileToTmp(context: Context, srcFile: File, tmpFile: File){

    val fullPathToApkFile = srcFile.absolutePath

    val tempFileParentPath = tmpFile.absolutePath
    val tempFilePath = tempFileParentPath + srcFile.name
    val apkTimestampTempFile = File(context.cacheDir, "apkTimestamp")
    apkTimestampTempFile.delete()
    apkTimestampTempFile.mkdirs()
    apkTimestampTempFile.createNewFile()

    val root = Runtime.getRuntime()
    root.exec("touch -r $fullPathToApkFile ${apkTimestampTempFile.absolutePath}/n")
    root.exec("mv $fullPathToApkFile $tempFileParentPath/n")
    root.exec("pm install -t -f $tempFilePath/n")
    root.exec("mv $tempFilePath $fullPathToApkFile/n")
    root.exec("touch -r ${apkTimestampTempFile.absolutePath} $fullPathToApkFile/n")

    apkTimestampTempFile.delete()
  }
  @Throws(IOException::class)
  private fun convertInputStreamToString(inputStream: InputStream): String? {
    val newLine = System.getProperty("line.separator")
    val result = java.lang.StringBuilder()
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
      var line: String?
      while (reader.readLine().also { line = it } != null) {
        result
          .append(line)
          .append(newLine)
      }
    }
    return result.toString()
  }


  fun downloadApkHttp(context: Context){
    val call: Call<ResponseBody?>? = Api.everon.downloadApk()
    LL.d("ApkCtrl::downloadApk() call: $call")

    call?.enqueue(object : Callback<ResponseBody?>{
      override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
        LL.d("ApkCtrl::onResponse() server contacted and has file")
//        response.body()?.let {
//          writeResponseBodyToDisk(context, it, "app-debug.apk")
//        }

        if (response.isSuccessful) {

          Thread {
            // do background stuff here
            context.run {
              response.body()?.let {
                val writtenToDisk = writeResponseBodyToDisk(
                  context,
                  it, "app-debug.apk"
                )
                LL.d("ApkCtrl::onResponse() writtenToDisk: $writtenToDisk")
              }
            }
          }.start()

        } else {
          LL.d("ApkCtrl::onResponse() server contact failed")
        }
      }

      override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
        LL.d("ApkCtrl::onFailure() error: $t")
      }

    })
  }

  fun system_reboot() {
    /*try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        } catch(Exception ex){
            Log.d("@combatspirit",ex.getMessage());
        }*/
    val pb = ProcessBuilder(*arrayOf("suc", "-c", "/system/bin/reboot"))
    var process: Process? = null
    try {
      process = pb.start()
      process.waitFor()
    } catch (e: IOException) {
      e.printStackTrace()
      LL.e("ApkCtrl::system_reboot: e: $e")
    } catch (e: InterruptedException) {
      e.printStackTrace()
      LL.e("ApkCtrl::system_reboot: e: ${e.message}")
    }
  }

}