package com.example.myboards.domain.provider

import android.content.Context
import android.content.SharedPreferences
import com.example.myboards.data.AuthServiceImpl
import com.example.myboards.data.FireBaseStorageServiceImpl
import com.example.myboards.data.GlideServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAuthService(
        sharedPreferences: SharedPreferences
    ) = AuthServiceImpl(
        sharedPreferences
    )

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences =
        context.getSharedPreferences("APP_SHARED_PREF", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFireBaseStorageService() = FireBaseStorageServiceImpl()

    @Provides
    @Singleton
    fun provideGlideService(
        @ApplicationContext context: Context,
        fireBaseStorageServiceImpl: FireBaseStorageServiceImpl
    ) = GlideServiceImpl(context, fireBaseStorageServiceImpl)

}