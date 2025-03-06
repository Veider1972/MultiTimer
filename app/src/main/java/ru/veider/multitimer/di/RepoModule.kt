package ru.veider.multitimer.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.veider.multitimer.const.DB_NAME
import ru.veider.multitimer.repository.CountersDB
import ru.veider.multitimer.repository.CountersRepository
import ru.veider.multitimer.repository.CountersRepositoryImpl

val repoModule = module {
	single<CountersDB> {
		Room
			.databaseBuilder(androidContext(), CountersDB::class.java, DB_NAME)
			.build()
	}
	singleOf(::CountersRepositoryImpl) { bind<CountersRepository>() }
}