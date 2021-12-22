package com.example.myboards.domain.provider

import android.content.Context
import com.example.myboards.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApiService(
        modelMapper: ModelMapper,
        executor: ApiRequestExecutor,
        endpoints: ApiEndpoints,
        authServiceImpl: AuthServiceImpl,
    ) = ApiServiceImpl(
        modelMapper,
        executor,
        endpoints,
        authServiceImpl
    )

    @Provides
    @Singleton
    fun provideApiEndpoints(
        retrofit: Retrofit
    ): ApiEndpoints = retrofit.create()

    @Provides
    @Singleton
    fun provideAppModelMapper() =
        ModelMapper()

    @Provides
    @Singleton
    fun provideApiRequestExecutor() = ApiRequestExecutor()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        client: OkHttpClient
    ): Retrofit = with(Retrofit.Builder()) {

        baseUrl("https://media-board.herokuapp.com/api/")
        addConverterFactory(GsonConverterFactory.create())
        client(client)
        build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        with(OkHttpClient.Builder()) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(logging)
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            pingInterval(30, TimeUnit.SECONDS)
            build()
        }
}