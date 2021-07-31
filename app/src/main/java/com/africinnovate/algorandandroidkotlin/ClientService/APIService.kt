package com.africinnovate.algorandandroidkotlin.ClientService

import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_TOKEN_KEY
import com.africinnovate.algorandandroidkotlin.utils.Constants.BASE_URL
import okhttp3.*
import okhttp3.OkHttpClient.Builder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class APIService @Inject constructor() {
    var spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA
        )
        .build()
    private fun httpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(AuthTokenInterceptor())
            .addNetworkInterceptor(CacheInterceptor())
            .addInterceptor(ForceCacheInterceptor())
            .connectionSpecs(Collections.singletonList(spec))
        return builder.build()
    }

    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient())
        .build()


    var service: AlgorandRESTService = retrofit.create(AlgorandRESTService::class.java)

}

class AuthTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("x-api-key", ALGOD_API_TOKEN_KEY)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

//Only if not cached from the server
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(10, TimeUnit.DAYS)
            .build()
        return response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}

class ForceCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
//        if (!isNetworkAvailable()) {
//            builder.cacheControl(CacheControl.FORCE_CACHE);
//        }
        return chain.proceed(builder.build());
    }


}








