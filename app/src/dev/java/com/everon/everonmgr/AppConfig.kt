package com.everon.everonmgr

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
