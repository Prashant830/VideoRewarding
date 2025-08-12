package com.example.videorewardingsystem.di

import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.example.videorewardingsystem.ui.viewmodels.ProfileViewModel
import com.example.videorewardingsystem.ui.viewmodels.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Single instance of PreferenceManager
    single { PreferenceManager(androidContext()) }

    // ViewModels
    viewModel { ProfileViewModel(get()) }
    viewModel { PlayerViewModel() }
    viewModel { SplashViewModel() }
}
