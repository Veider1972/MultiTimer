package ru.veider.multitimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CountersViewModelFactory:ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(CountersViewModel::class.java) -> CountersViewModel.getInstance()
                else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
            }
        } as T
    }

    companion object{
        private var instance: CountersViewModelFactory? = null
        fun getInstance() = instance ?: synchronized(CountersViewModelFactory::class.java) {
            instance ?: CountersViewModelFactory().also { instance = it }
        }
    }


}