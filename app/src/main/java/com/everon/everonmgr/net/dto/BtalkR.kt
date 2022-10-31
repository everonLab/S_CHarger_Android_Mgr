package com.everon.everonmgr.net.dto

import androidx.annotation.Keep
import retrofit2.Response

/**
 * <p><h3>BTalk API response</h3></p>
 * Created by kmb on 6/15/2018.
 */
@Keep
data class BtalkR(
  var msg: BtalkMsg
) {
  companion object {
    fun get(response: Response<BtalkR>): BtalkR? {
      if (!response.isSuccessful || response.body() == null) return null
      return response.body()
    }
  }
}

@Keep
data class BtalkMsg(
  var evtCode: String,
  var uid: String,
  var result: String,
  var result_msg: String,
  var errMsg: String,
  var user_sno: String
) {
  companion object {
    fun get(response: Response<BtalkR>): BtalkMsg? {
      if (!response.isSuccessful || response.body() == null) return null
      return response.body()?.msg
    }
  }
}
