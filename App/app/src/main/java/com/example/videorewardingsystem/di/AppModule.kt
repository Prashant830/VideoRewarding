package com.example.videorewardingsystem.di

import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.networklayer.Repository
import com.example.videorewardingsystem.networklayer.retrofit.ApiService
import com.example.videorewardingsystem.networklayer.retrofit.PredictionApiService
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.example.videorewardingsystem.ui.viewmodels.ProfileViewModel
import com.example.videorewardingsystem.ui.viewmodels.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // Base URL and Retrofit for first API
    single { "https://6216f32009a8.ngrok-free.app/home/" }
    single {
        Retrofit.Builder()
            .baseUrl(get<String>())
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }

    // Base URL and Retrofit for Roboflow
    single(named("secondBaseUrl")) { "https://detect.roboflow.com/" }
    single(named("secondRetrofit")) {
        Retrofit.Builder()
            .baseUrl(get<String>(named("secondBaseUrl")))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<PredictionApiService> { get<Retrofit>(named("secondRetrofit")).create(PredictionApiService::class.java) }

    // Repository
    single { Repository(get<ApiService>(), get<PredictionApiService>()) }

    // Preference Manager
    single { PreferenceManager(androidContext()) }

    // ViewModels
    viewModel { ProfileViewModel(get()) }
    viewModel { PlayerViewModel(get(), get()) }
    viewModel { SplashViewModel() }
    viewModel { HomeViewModel(get(), get()) }  // Repository & PreferenceManager
}


