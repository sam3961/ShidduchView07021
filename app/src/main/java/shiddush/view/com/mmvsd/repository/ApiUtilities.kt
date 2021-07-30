package shiddush.view.com.mmvsd.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import shiddush.view.com.mmvsd.BuildConfig
import java.util.concurrent.TimeUnit

/**
 * Created by Sumit Kumar.
 * @B ApiUtilities :  This class contain the Base url of server as well as singletone values required.
 **/

class ApiUtilities {
    companion object {

        private val httpClient = OkHttpClient().newBuilder()
        private val builder = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

        fun getAPIService(): APIService {
            httpClient.addInterceptor(getLoggingInterceptor())
            httpClient.addInterceptor { chain ->
                val requestBuilder = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            val client = httpClient.connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS).build()
            val retrofit = builder.client(client).build()
            return retrofit.create(APIService::class.java)
        }

        private fun getLoggingInterceptor(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            return logging
        }
    }
}