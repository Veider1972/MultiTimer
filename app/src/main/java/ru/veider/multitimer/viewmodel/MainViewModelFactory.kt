package ru.veider.multitimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory:ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel.getInstance()
                isAssignableFrom(PreferenceViewModel::class.java) -> PreferenceViewModel.getInstance()
                else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
            }
        } as T
    }

    companion object{
        private var instance: MainViewModelFactory? = null
        fun getInstance() = instance ?: synchronized(MainViewModelFactory::class.java) {
            instance ?: MainViewModelFactory().also { instance = it }
        }
    }


}