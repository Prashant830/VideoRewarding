package com.example.videorewardingsystem.di

import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.example.videorewardingsystem.ui.viewmodels.ProfileViewModel
import com.example.videorewardingsystem.ui.viewmodels.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SplashViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { PlayerViewModel() }
}
