package com.everon.everonmgr.net.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


/**
 * <p><h3>Firebase rest api</h3></p>
 * Created by kmb on 6/1/2018.
 */
interface EveronApi {
  //---------------------------------------------------
  // apk
  //---------------------------------------------------
//  @FormUrlEncoded
//  @POST("/")
//  fun downloadApk(
////    @Field("token") token: String
//  ): Call<ResponseBody>

  @Streaming
  @GET("/test.txt")
  fun downloadTxt(): Call<ResponseBody?>?

  @Streaming
  @GET("/app-debug.apk")
  fun downloadApk(): Call<ResponseBody?>?

  //---------------------------------------------------
  // log
  //---------------------------------------------------
//  @Multipart
//  @POST("/upload/")
//  fun uploadFile(
//    @PartMap partMap: LinkedHashMap<String, RequestBody>?,
//    @Part names: List<MultipartBody.Part?>?
//  ): Call<JsonObject?>?

  @Multipart
  @POST("/upload")
  fun uploadFile(
    @Part("description") description: RequestBody?,
    @Part file: MultipartBody.Part?
  ): Call<ResponseBody?>?

//  @Multipart
//  @POST("/upload/")
//  fun uploadFile(
//    @Part file: MultipartBody.Part?
//  ): Call<ResponseBody?>?

  //---------------------------------------------------
  // Search
  //---------------------------------------------------
//  /**
//   * 팀 검색
//   */
//  @POST("/btalk/api/search/team")
//  fun searchTeam(
//    @Body() searchReq: SearchReq
//  ): Call<SearchR<SearchTeam>>
//
//  /**
//   * 부서 검색
//   */
//  @POST("/btalk/api/search/dept")
//  fun searchPart(
//    @Body() searchReq: SearchReq
//  ): Call<SearchR<SearchPart>>
//
//  /**
//   * 사용자 검색
//   *  - 사용자ID 중복검색은 user_key 만 필요
//   */
//  @POST("/btalk/api/search/user")
//  fun searchUser(
//    @Body() searchReq: SearchReq
//  ): Call<SearchR<SearchUser>>

}











