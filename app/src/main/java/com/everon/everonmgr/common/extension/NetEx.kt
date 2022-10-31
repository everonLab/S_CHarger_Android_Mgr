package com.everon.everonmgr.common.extension

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonFac{
  fun createBasic(): Gson{
    return GsonBuilder()
      .setLenient()
      .create()
  }
}