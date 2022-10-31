package com.everon.everonmgr.common.dto

import androidx.annotation.Keep

@Keep
data class FtpFileInfo(
  // ftp
  var ftp_host: String,
  var ftp_port: Int,
  var ftp_id: String,
  var ftp_pass: String,

  // file
  var file_path: String,
  var file_name: String
){
  companion object {}
}

fun FtpFileInfo.Companion.createDummy(): FtpFileInfo{
  return FtpFileInfo(
    "ftps.everon.co.kr",
    62000,
    "charger",
    "EveronCharger2022",
    "/ftp/files",
    "test0.apk"
  )
}

fun FtpFileInfo.Companion.createFileNameOnlyDummy(fileName: String): FtpFileInfo{
  return FtpFileInfo(
    "",
    0,
    "",
    "",
    "",
    fileName
  )
}