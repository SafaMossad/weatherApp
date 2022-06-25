import com.wecancity.robusta_weather_app.networking.RemoteApiService
import com.wecancity.robusta_weather_app.viewUtils.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun buildClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY

        }).addInterceptor(
            buildAuthorizationInterceptor()
        )
        .build()

fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        //for converting moshi
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .build()
}

fun buildAuthorizationInterceptor() = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //if there is token send it else send new one
        val originalRequest = chain.request()
        // val new = originalRequest.newBuilder().build()
        return chain.proceed(originalRequest)
    }
}

fun buildApiService(): RemoteApiService = buildRetrofit().create(RemoteApiService::class.java)
