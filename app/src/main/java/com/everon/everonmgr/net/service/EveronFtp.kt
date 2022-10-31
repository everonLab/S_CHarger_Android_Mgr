package com.everon.everonmgr.net.service

import android.os.SystemClock
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.dto.FtpFileInfo
import com.everon.everonmgr.common.listener.FileDownloadListener
import com.everon.everonmgr.util.LL
import it.sauronsoftware.ftp4j.FTPClient
import it.sauronsoftware.ftp4j.FTPCommunicationListener
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

object EveronFtp {
  //---------------------------------------------------
  // file
  //---------------------------------------------------
  private var downloadRetryCount: Int = 0

  // TODO:  add listener
  fun download(
    ftpFileInfo: FtpFileInfo,
    file: File,
    maxRetryCount: Int = 0,
    fileDownloadListener: FileDownloadListener? = null
  ){
    // init
    downloadRetryCount = 0

    // start
    _download(ftpFileInfo, file, maxRetryCount, fileDownloadListener)
  }

  private fun _download(
    ftpFileInfo: FtpFileInfo,
    file: File,
    maxRetryCount: Int = 0,
    fileDownloadListener: FileDownloadListener? = null
  ){
    Thread {
      var isFail = false
      var failMsg: String = ""
      var time = 0L

      // do background stuff here
      try {
        val client = FTPClient()

        client.autoNoopTimeout = 1_000_000L

        client.connect(ftpFileInfo.ftp_host, ftpFileInfo.ftp_port)
        client.login(ftpFileInfo.ftp_id, ftpFileInfo.ftp_pass)
        client.type = FTPClient.TYPE_BINARY
        client.changeDirectory(ftpFileInfo.file_path)
        val fileSize = getFileSize(client, ftpFileInfo.file_path, ftpFileInfo.file_name)

        var received = 0L

        client.download(file.name, file, (object: FTPDataTransferListener{
          override fun started() {
            LL.d("EveronFtp::started()")
            time = SystemClock.elapsedRealtime()
          }

          override fun transferred(p0: Int) {
            received += p0
            LL.d("EveronFtp::transferred() p0: $p0, received: $received, fileSize: $fileSize")

            fileDownloadListener?.onReceived(received, fileSize)
          }

          override fun completed() {
            LL.d("EveronFtp::completed() ")
            fileDownloadListener?.onComplete(true, file, "completed")
          }

          override fun aborted() {
            LL.d("EveronFtp::aborted() ")
            fileDownloadListener?.onComplete(false, file, "aborted")
          }

          override fun failed() {
            LL.d("EveronFtp::failed() downloadRetryCount: $downloadRetryCount, maxRetryCount: $maxRetryCount")
            isFail = true
            failMsg = "failed"
          }

        }))
        client.disconnect(true)

        LL.d("EveronFtp::download() file..exists: ${file.exists()}")
      } catch (e: Exception) {
        LL.d("EveronFtp::download() e: ${e.message}")
        isFail = true
        failMsg = "${e.message}"
      }

      // --------------- retry
      if (isFail){
        // retry
        if (downloadRetryCount < maxRetryCount){
          downloadRetryCount++
          // delay
          Timer().schedule(timerTask {
            _download(ftpFileInfo, file, maxRetryCount, fileDownloadListener)
          }, Config.DOWNLOAD_APK_RETRY_DELAY)
        }
        // retry over
        else{
          fileDownloadListener?.onComplete(false, file, failMsg)
          time = SystemClock.elapsedRealtime() - time
          LL.d("EveronFtp::failed() time: $time")
        }
      }

    }.start()
  }

  fun upload(ftpFileInfo: FtpFileInfo, file: File){
    Thread {
      // do background stuff here
      try {
        val client = FTPClient()
        client.connect(ftpFileInfo.ftp_host, ftpFileInfo.ftp_port)
        client.login(ftpFileInfo.ftp_id, ftpFileInfo.ftp_pass)
        client.type = FTPClient.TYPE_BINARY
        client.changeDirectory(ftpFileInfo.file_path)

        // listener
        client.addCommunicationListener(object : FTPCommunicationListener{
          override fun sent(p0: String?) {
            LL.d("EveronFtp::sent() p0: $p0")
          }

          override fun received(p0: String?) {
            LL.d("EveronFtp::received() p0: $p0")
          }

        })

        LL.d("EveronFtp::upload() 1 file.exists: ${file.exists()}")
        LL.d("EveronFtp::upload() client.isPassive: ${client.isPassive}")
//        client.command1
        
        client.upload(file)
        client.disconnect(true)

        LL.d("EveronFtp::upload() 2 file.exists: ${file.exists()}")

      } catch (e: Exception) {
        LL.d("EveronFtp::upload() e: ${e.message}")
      }

    }.start()
  }

  fun getFileSize(ftp: FTPClient, filePath: String, fileName: String): Long{
    return ftp.fileSize("$filePath/$fileName")
  }

}