package com.everon.everonmgr.common

/**
 * global 설정
 * Charge App & Manager App 공용 사용
 */
object Config {
  //---------------------------------------------------
  // Heartbeat
  //---------------------------------------------------
  /**
   *  Heartbeat 주기 (단위: ms)
   */
  const val HEARTBEAT_PERIOD: Long = 6_000L // ms

  /**
   *  Charge App > Manager App 으로 보내는 Heartbeat 없음을 판단하는 시간 (단위: ms)
   *  시용처: Manager App
   *  관리엡에서는 이 시간 동안 Heartbeat 이 들어오지 않으면 충전앱 이상으로 판단하고 충전앱을 실행 시킴
   */
  const val HEARTBEAT_NOT_RECEIVED_TIME: Long = 36_000L // ms

  /**
   *  Manager App > Charge App 으로 보내는 Heartbeat 응답이 없음을 판단하는 시간 (단위: ms)
   *  시용처: Charge App
   *  충전엡에서는 이 시간 동안 Heartbeat 응답이 들어오지 않으면 관리앱 이상으로 판단. (판단후 조치는 미정)
   */
  const val HEARTBEAT_NOT_RESPONSE_TIME: Long = 36_000L // ms

  /**
   * Charge App ID
   *
   * 실제 적용시에는 com.speel.SerialTester 로 변경 필요
   */
  const val CHARGE_APP_ID = "com.speel.SerialTester"

  /** Manager App ID */
  const val MGR_APP_ID = "com.everon.everonmgr"

  // ------------------ action
  /** Charge App 에서 보내는 Heartbeat 관련 action. 세부 내용은 @see [IntentKey] 침고 */
  const val CHARGE_APP_HEARTBEAT_ACTION = "${CHARGE_APP_ID}.HEARTBEAT"

  /** Manager App 에서 보내는 Heartbeat 관련 action. 세부 내용은 @see [IntentKey] 침고 */
  const val MGR_APP_HEARTBEAT_ACTION = "${MGR_APP_ID}.HEARTBEAT"

  //---------------------------------------------------
  // Install
  //---------------------------------------------------
  /** apk file path. /storage/emulated/0 와 같음 */
  //const val APK_DOWNLOAD_PATH = "/sdcard/DCIM"
  const val APK_DOWNLOAD_PATH = "/sdcard/Download"

  // ------------------ action
  /** Charge App 에서 보내는 Install 관련 action. 세부 내용은 @see [IntentKey] 침고 */
  const val CHARGE_APP_INSTALL_ACTION = "${CHARGE_APP_ID}.INSTALL"

  /** Manager App 에서 보내는 Install 관련 action. 세부 내용은 @see [IntentKey] 침고 */
  const val MGR_APP_INSTALL_ACTION = "${MGR_APP_ID}.INSTALL"

  /** Download apk retry count */
  const val DOWNLOAD_APK_MAX_RETRY_COUNT: Int = 2

  /** Install apk retry count */
  const val INSTALL_APK_MAX_RETRY_COUNT: Int = 2

  /** Download apk retry 시도시 지연 시간 */
  const val DOWNLOAD_APK_RETRY_DELAY: Long = 1000L // ms

  /** Install apk retry 시도시 지연 시간 */
  const val INSTALL_APK_RETRY_DELAY: Long = 1000L // ms

  /** Charge App launch 지연 시간 */
  const val LAUNCH_APP_DELAY: Long = 2000L // ms
}