package com.everon.everonmgr.common

/**
* IntentKey 정의
* Charge App & Manager App 공용 사용
*/
object IntentKey {
  val heartbeat = HeartbeatIntentKey
  val install = InstallIntentKey
}

//---------------------------------------------------
// Heartbeat
//---------------------------------------------------
object HeartbeatIntentKey{
  /**
   * Charge App > Manager App
   * Charge App 에서 발송하는 Heartbeat
   * type: Long
   * value: elapsedRealtime()
   */
  const val HEARTBEAT: String = "HEARTBEAT"

  /**
   * Manager App > Charge App
   * Heartbeat를 받으면 다시 Charge App 으로 응답
   * type: Long
   * value: elapsedRealtime()
   */
  const val ON_RESPONSE_HEARTBEAT: String = "ON_RESPONSE_HEARTBEAT"
}

//---------------------------------------------------
// Install
//---------------------------------------------------
object InstallIntentKey{
  /**
   * Charge App > Manager App
   * apk file 경로. ftp 주소는 제외
   * type: FtpFileInfo
   */
  const val DOWNLOAD_APK: String = "DOWNLOAD_APK"

  /**
   * Charge App > Manager App
   * apk install 하라는 명령
   * type: FtpFileInfo
   */
  const val INSTALL_APK: String = "INSTALL_APK"

  /**
   * Charge App > Manager App
   * apk download, install 실패시 재시도 횟수
   * type: Int
   */
  const val MAX_RETRY_COUNT: String = "MAX_RETRY_COUNT"

  /**
   * Manager App > Charge App
   * apk download 진행시 이벤트. Charge App 에서 프로그래스 바등에 활용 가능
   * type: long[]
   * value: [0]: received, [1] 전체 fileSize (단위: Byte)
   */
  const val ON_UPDATE_DOWNLOAD_APK: String = "ON_UPDATE_DOWNLOAD_APK"

  /**
   * Manager App > Charge App
   * apk download 완료(성공) 이벤트
   * type: Boolean
   * value: true
   */
  const val ON_SUCCESS_DOWNLOAD_APK: String = "ON_SUCCESS_DOWNLOAD_APK"

  /**
   * Manager App > Charge App
   * apk download 완료(실패) 이벤트
   * type: String
   * value: error message
   */
  const val ON_FAILURE_DOWNLOAD_APK: String = "ON_FAILURE_DOWNLOAD_APK"

  /**
   * Manager App > Charge App
   * apk install 완료(실패) 이벤트
   *  - Charge App이 덮어 써짐으로 install 과정이나 성공이벤트는 전해 줄수 없다
   *  - install 성공여부는 관리앱에서 충전앱 설치후 최초 실행시 intent에 LAUNCH_APK 로 알려준다
   * type: String
   * value: error message
   */
  const val ON_FAILURE_INSTALL_APK: String = "ON_FAILURE_INSTALL_APK"


  /**
   * Manager App > Charge App
   * apk install 성공 후 처음 실행시 이벤트
   * type: String
   * value: apk name ex) EVSAF122.apk
   */
  const val LAUNCH_APK: String = "LAUNCH_APK"

  /**
   * Charge App > Manager App
   * apk 실행 완료(성공) 이벤트
   * type: String
   * value: apk name ex) EVSAF122.apk
   */
  const val ON_SUCCESS_LAUNCH_APK: String = "ON_SUCCESS_LAUNCH_APK"

  /**
   * Charge App > Manager App
   * apk 실행 완료(실패) 이벤트
   * type: String
   * value: apk name ex) EVSAF122.apk
   */
  const val ON_FAILURE_LAUNCH_APK: String = "ON_FAILURE_LAUNCH_APK"
}
