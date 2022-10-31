package com.everon.everonmgr.net.ctrl

import android.content.Context
import com.everon.everonmgr.net.Api
import com.everon.everonmgr.net.util.FileUtil
import com.everon.everonmgr.util.LL
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


object LogCtrl {
  //---------------------------------------------------
  // upload
  //---------------------------------------------------
  fun uploadLog(context: Context){
    uploadTest(context)
  }

  private fun uploadTest(context: Context){
    val file = File(context.getExternalFilesDir(null), "test.txt")
    uploadFile(context, file)
  }

  private fun uploadFile(context: Context, file: File) {
    LL.d("LogCtrl::fileUpload() file: $file, file.exist: ${file.exists()}")

    val fileUri = FileUtil.uriFromFile(context, file)

    val requestFile: RequestBody = RequestBody.create(
      fileUri?.let { uri ->
        context.contentResolver.getType(uri)?.let {
          it.toMediaTypeOrNull()
        }
      },
      file
    )

    // MultipartBody.Part is used to send also the actual file name

    // MultipartBody.Part is used to send also the actual file name
    val body: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
      "picture", file.name, requestFile)

    // add another part within the multipart request

    // add another part within the multipart request
    val descriptionString = "hello, this is description speaking"
    val description = RequestBody.create(
      MultipartBody.FORM, descriptionString
    )

    // finally, execute the request

    // finally, execute the request
    val call: Call<ResponseBody?>? = Api.everon.uploadFile(description, body)
    call?.enqueue(object : Callback<ResponseBody?> {
      override fun onResponse(
        call: Call<ResponseBody?>,
        response: Response<ResponseBody?>
      ) {
        LL.d("LogCtrl::onResponse() success")
      }

      override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
        LL.d("LogCtrl::onResponse() ERR t: ${t.message}")
      }
    })
  }
}