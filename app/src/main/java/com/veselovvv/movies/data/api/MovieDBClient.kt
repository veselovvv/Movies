package com.veselovvv.movies.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val API_KEY_PARAMETER_NAME = "api_key"
private const val API_KEY = "YourApiKey"
private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"

object MovieDBClient {
    fun getClient(): MovieDBI {
        val requestInterceptor = Interceptor { chain ->
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter(API_KEY_PARAMETER_NAME, API_KEY)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieDBI::class.java)
    }

    fun getPosterBaseUrl() = POSTER_BASE_URL
}