package ru.veider.multitimer.di

import com.arttttt.nav3router.Router
import org.koin.dsl.module
import ru.veider.multitimer.navigation.Screen

val navigationModule = module {
    single { Router<Screen>() }
}