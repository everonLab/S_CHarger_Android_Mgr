package com.everon.everonmgr.common.listener

import java.io.File

interface FileDownloadListener {
  fun onReceived(received: Long, fileSize: Long)
  fun onComplete(success: Boolean, file: File?, message: String?)
}

interface FileUploadListener {
  fun onSent(sent: Long, fileSize: Long)
  fun onComplete(success: Boolean, file: File?, message: String?)
}