package com.everon.everonmgr.common

object Debug {
  // -------------------------- debug flags
  const val GLOBAL_DEBUG_ON = true // release시 false로

  /** Log 사용 여부. 주의! - GLOBAL_DEBUG_ON 의 영향을 받지 않음 직접 변경 필요 */
  const val GLOBAL_LOG_ON = true

  const val USE_RETROFIT_LOG = true && GLOBAL_DEBUG_ON
}