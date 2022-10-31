package com.hundredsoft.btalk.net.url

import com.everon.everonmgr.AppConfig


/**
 * <p><h3>Firebase Url</h3></p>
 * Created by kmb on 6/1/2018.
 */
class EverOnUri {
  companion object {

    //---------------------------------------------------
    // apk server
    //---------------------------------------------------
    // host ext
    private const val EVERON_CONFIG_HOST_TMP: String = ""
    private const val EVERON_CONFIG_HOST: String = AppConfig.apkServer

    // host
    const val EVERON_HOST: String = EVERON_CONFIG_HOST
  }
}