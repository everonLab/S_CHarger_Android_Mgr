package com.everon.everonmgr.ctrl

import android.content.Context
import android.content.IntentFilter
import android.os.SystemClock.elapsedRealtime
import com.everon.everonmgr.common.Config
import com.everon.everonmgr.common.IntentKey
import com.everon.everonmgr.receiver.BasicReceiver
import com.everon.everonmgr.util.LL
import com.everon.everonmgr.sender.Sender
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object HeartbeatCtrl {
  //---------------------------------------------------
  // const Properties
  //---------------------------------------------------
  private const val initDelay: Long = 1_000L // ms

  //---------------------------------------------------
  // public Properties
  //---------------------------------------------------
  // state
  enum class State{
    start, stop, on_timer, on_heartbeat, no_heartbeat
  }
  var stateListener: ((state: State) -> Unit)? = null

  //---------------------------------------------------
  // private Properties
  //---------------------------------------------------
  // receiver
  private var receiver: BasicReceiver? = null

  // heartbeat
  // Create an executor that executes tasks in a background thread.
  private var backgroundExecutor: ScheduledExecutorService? = null
  private var count: Int = 0
  private var lastHeartbeatReceiveTime: Long? = null

  //---------------------------------------------------
  // init
  //---------------------------------------------------

  //---------------------------------------------------
  // public methods
  //---------------------------------------------------
  fun start(context: Context) {
    changeState(context, State.start)
  }

  fun stop(context: Context){
    changeState(context, State.stop)
  }

  //---------------------------------------------------
  // state
  //---------------------------------------------------
  private fun changeState(context: Context, state: State){
    when(state){
      State.start -> {
        registerReceiver(context)
        startListen(context)
      }

      State.stop ->{
        unRegisterReceiver(context)
        stopListen(context)
      }

      State.on_timer ->{
        if (lastHeartbeatReceiveTime == null){
          lastHeartbeatReceiveTime = elapsedRealtime()
        }
        lastHeartbeatReceiveTime?.let {
          val diffTime = elapsedRealtime() - it
          if (diffTime >= Config.HEARTBEAT_NOT_RECEIVED_TIME) {

            // chk install state
            val isInstallOrLaunchState = InstallCtrl.isInatallOrLaunchState()
            if (isInstallOrLaunchState){
              // install, launch state 일경우는 no_heartbeat 발생 방지를 위해 lastHeartbeatReceiveTime를 업데이트
              lastHeartbeatReceiveTime = elapsedRealtime()
            }else{
              changeState(context, State.no_heartbeat)
            }

            return
          }
        }

      }

      State.on_heartbeat ->{
        lastHeartbeatReceiveTime = elapsedRealtime()

        // send response
        Sender.sendHeartbeatResponse(context)
      }

      State.no_heartbeat ->{
        // stop
        /**
        unRegisterReceiver(context)
        stopListen(context)
        */
        lastHeartbeatReceiveTime = null
      }
      else -> {}
    }
    stateListener?.invoke(state)
  }

  //---------------------------------------------------
  // scheduler
  //---------------------------------------------------
  private fun startListen(context: Context){
    stopListen(context)

// repeat
    backgroundExecutor = Executors.newSingleThreadScheduledExecutor()
    backgroundExecutor?.scheduleAtFixedRate({
      count ++
//      LL.d("HeartbeatCtrl::startListen() count: $count")
      changeState(context, State.on_timer)
    }, initDelay.toLong(), Config.HEARTBEAT_PERIOD, TimeUnit.MILLISECONDS)
  }

  private fun stopListen(context: Context){
    backgroundExecutor?.shutdown()
    count = 0
    lastHeartbeatReceiveTime = null
  }

  //---------------------------------------------------
  // receiver
  //---------------------------------------------------
  private fun createReceiver(context: Context){
    receiver = BasicReceiver()
    receiver?.onReceiveListener = {
      LL.d("HeartbeatCtrl::createReceiver() it: $it")
      it?.let {
        it.extras?.getLong(IntentKey.heartbeat.HEARTBEAT)?.let {
          changeState(context, State.on_heartbeat)
        }
      }
    }
  }

  private fun registerReceiver(context: Context){
    if (receiver != null) unRegisterReceiver(context)
    if (receiver == null) createReceiver(context)
    context.registerReceiver(receiver, IntentFilter(Config.CHARGE_APP_HEARTBEAT_ACTION))
  }

  private fun unRegisterReceiver(context: Context){
    try {
      context.unregisterReceiver(receiver)
    }catch (e: Exception){
      LL.d("HeartbeatCtrl::unRegisterReceiver() e: $e")
    }
  }

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------
}