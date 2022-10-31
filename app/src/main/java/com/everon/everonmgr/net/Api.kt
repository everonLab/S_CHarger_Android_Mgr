package com.everon.everonmgr.net

import com.google.gson.GsonBuilder
import com.everon.everonmgr.AppConfig
import com.everon.everonmgr.common.Debug
import com.everon.everonmgr.net.service.EveronApi
import com.hundredsoft.btalk.net.url.EverOnUri
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * <p><h3>All API</h3></p>
 * Created by kmb on 6/1/2018.
 */
object Api {
  //---------------------------------------------------
  // const Properties
  //---------------------------------------------------

  //---------------------------------------------------
  // private Properties
  //---------------------------------------------------
  private val EVERON_BASE_URL = EverOnUri.EVERON_HOST

  //---------------------------------------------------
  // override, implement
  //---------------------------------------------------

  //---------------------------------------------------
  // EverOn
  //---------------------------------------------------
  var everon = createEveronApi(createTestEveronHeaderMap())

  private fun createEveronApi(headerMap: Map<String, String>): EveronApi {
//    LL.d("Api::createBTalkApi() headerMap: ", headerMap)
    // OkHttpClient
    val client = createEveronClient(headerMap, AppConfig.acceptAllSSL)
//    LL.d("Api::createEveronApi() client: $client")
    // Gson
    val gson = GsonBuilder()
      .setLenient()
//                .disableHtmlEscaping()
      .create()

    // Retrofit
    val retrofit = Retrofit.Builder()
      .baseUrl(EVERON_BASE_URL)
//      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .client(client)
      .build()

//    LL.d("Api::createEveronApi() retrofit: $retrofit")
    
    return retrofit.create(EveronApi::class.java)
  }

  private fun createEveronClient(
    headerMap: Map<String, String>,
    acceptAllSSL: Boolean = false
  ): OkHttpClient {
    if (acceptAllSSL) {
      return createEveronClientAcceptAllSSL(headerMap)
    }
//    LL.d("Api::createEveronClient() ")
    val builder = OkHttpClient().newBuilder()
    builder.readTimeout(60, TimeUnit.SECONDS)
    builder.connectTimeout(60, TimeUnit.SECONDS)

//        val sslContext = SSLContext.getInstance("TLS")
//        val trustManagers = arrayOf<TrustManager>()
//        sslContext.init(null, trustManagers, null)

    // debug
    if (Debug.USE_RETROFIT_LOG) {
      val interceptor = HttpLoggingInterceptor()
      interceptor.level = HttpLoggingInterceptor.Level.BASIC
      builder.addInterceptor(interceptor)
    }
//    LL.d("Api::createEveronClient() builder: $builder")
    builder.addInterceptor { chain ->
      val reqBuilder = chain.request().newBuilder()

      // put to header
      for (entry in headerMap.entries) {
        reqBuilder.addHeader(entry.key, entry.value)
      }
      val request = reqBuilder.build()
      chain.proceed(request)
    }


    return builder.build()
  }

  private fun createEveronClientAcceptAllSSL(headerMap: Map<String, String>): OkHttpClient {
    try {
      // Create a trust manager that does not validate certificate chains
      val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
          return emptyArray()
        }

        override fun checkClientTrusted(
          chain: Array<java.security.cert.X509Certificate>,
          authType: String
        ) {
        }

        override fun checkServerTrusted(
          chain: Array<java.security.cert.X509Certificate>,
          authType: String
        ) {
        }
      })

      // Install the all-trusting trust manager
      val sslContext = SSLContext.getInstance("SSL")
      sslContext.init(null, trustAllCerts, java.security.SecureRandom())

      // Create an ssl socket factory with our all-trusting manager
      val sslSocketFactory = sslContext.socketFactory

      val builder = OkHttpClient.Builder()
      builder.readTimeout(60, TimeUnit.SECONDS) // 2022/07/25 btalk 10 (https://stackoverflow.com/a/29380845)
      builder.connectTimeout(60, TimeUnit.SECONDS) // 2022/07/25 btalk 5
      builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)

      /** 19.8.6
      builder.hostnameVerifier { hostname, session -> true }
       */
      builder.hostnameVerifier(HostnameVerifier { hostname, session -> true })

      builder.addInterceptor { chain ->
        val reqBuilder = chain.request().newBuilder()

        // put to header
        for (entry in headerMap.entries) {
          reqBuilder.addHeader(entry.key, entry.value)
        }
        val request = reqBuilder.build()
        chain.proceed(request)
      }

      return builder.build()
    } catch (e: Exception) {
      throw RuntimeException(e)
    }

  }

  //---------------------------------------------------
  // header
  //---------------------------------------------------
//    fun createFireHeaderMap(apikey: String): Map<String, String> {
//        val header = HashMap<String, String>()
//        header["authorization"] = apikey
//        return header
//    }
//
//    fun createFireAccessKeyHeaderMap(accesskey: String, apikey: String): Map<String, String> {
//        val header = HashMap<String, String>()
//        header["accesskey"] = accesskey
//        //        header.put("apikey", apikey);
//        return header
//    }


  private fun createTestEveronHeaderMap(): Map<String, String> {
    val header = HashMap<String, String>()
    return header
  }

  //---------------------------------------------------
  // private methods
  //---------------------------------------------------

}