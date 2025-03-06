package ru.veider.multitimer.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.veider.multitimer.viewmodel.*

val appModule = module {
    singleOf(::MainViewModel)
    viewModelOf(::PreferenceViewModel)
}