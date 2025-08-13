package com.example.videorewardingsystem.di

import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.networklayer.Repository
import com.example.videorewardingsystem.networklayer.retrofit.ApiService
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.example.videorewardingsystem.ui.viewmodels.ProfileViewModel
import com.example.videorewardingsystem.ui.viewmodels.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single { "http://10.0.2.2:8080/home/" }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>())
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }

    single { Repository(get()) }

    // ✅ Preference Manager
    single { PreferenceManager(androidContext()) }

    // ✅ ViewModels
    viewModel { ProfileViewModel(get()) } // Needs Repository
    viewModel { PlayerViewModel() }
    viewModel { SplashViewModel() }
    viewModel { HomeViewModel(get(),get()) }
}
