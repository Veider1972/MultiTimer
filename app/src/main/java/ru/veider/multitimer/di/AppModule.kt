package ru.veider.multitimer.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.veider.multitimer.data.preferences.PreferencesImpl
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.viewmodel.*

val appModule = module {
    single<Preferences>{ PreferencesImpl(get()) }
    singleOf(::MainViewModel)
}