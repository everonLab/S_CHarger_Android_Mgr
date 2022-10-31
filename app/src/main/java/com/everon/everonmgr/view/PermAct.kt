package com.everon.everonmgr.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.everon.everonmgr.R.*
import com.everon.everonmgr.util.LL


//import androidx.co

const val PERMISSION_REQUEST_CODE = 1
const val PERMISSION_REQUEST_CODE2 = 2

class PermAct : ComponentActivity() {

  private fun showDialog(titleText: String, messageText: String) {
    with(androidx.appcompat.app.AlertDialog.Builder(this)) {
      title = titleText
      setMessage(messageText)
      setPositiveButton(string.common_ok) { dialog, _ ->
        dialog.dismiss()
      }
      show()
    }
  }

  private fun requestPermission() {
    val intent = Intent(
      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//      Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
      Uri.parse("package:$packageName")
    )
    try {
      startActivityForResult(intent, PERMISSION_REQUEST_CODE)
    } catch (e: Exception) {
      showDialog(
        getString(string.permission_error_title),
        getString(string.permission_error_text)
      )
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setContent {
//      createContent()
//    }
    setContentView(layout.activity_perm)

    var btn = findViewById<Button>(id.btn)
    /**
    btn.setOnClickListener{
      requestPermission()
    }
    */
    btn.setOnClickListener {
      finish()
    }

  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    // Don't check for resultCode == Activity.RESULT_OK because the overlay activity
    // is closed with the back button and so the RESULT_CANCELLED is always received.
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (drawOverOtherAppsEnabled()) {
        // The permission has been granted.
        // Resend the last command - we have only one, so no additional logic needed.

//        startFloatingService(INTENT_COMMAND_NOTE)

//        finish()

        ActivityCompat.requestPermissions(
          this,
          arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
          ),
          PERMISSION_REQUEST_CODE2
        )
        LL.d("PermAct::onActivityResult() requestPermissions")
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }

}