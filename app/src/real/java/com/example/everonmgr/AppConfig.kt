package com.example.everonmgr

/**
 * <p><h3>Config - using in productFlavors</h3></p>
 * Created by kmb on 7/25/2022.
 */
open class AppConfig {
  //---------------------------------------------------
  // Properties
  //---------------------------------------------------
  companion object {
    // server - test
    const val apkServer: String = "http://172.30.1.55:8000"
    const val acceptAllSSL: Boolean = false

    // ftp
    /** 22.8.30 client app 에서 받는것으로 변경
    const val ftpHost: String = "ftps.everon.co.kr"
    const val ftpPort: Int = 62000
    const val ftpId: String = "charger"
    const val ftpPass: String = "EveronCharger2022"
    const val ftpApkPath: String = "/ftp/files"
    */
  }

  //---------------------------------------------------
  // override, implement
  //---------------------------------------------------

  //---------------------------------------------------
  // public methods
  //---------------------------------------------------

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------

}
