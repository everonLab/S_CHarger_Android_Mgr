package com.everon.everonmgr.net.dto

data class Client(
  var clientPackageName: String?,
  var clientProcessId: String,
  var clientData: String?,
  var ipcMethod: String
)

object RecentClient {
  var client: Client? = null
}