package ru.veider.multitimer.service

import androidx.lifecycle.*
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

open class LifecycleAndViewStoreOwnerService : LifecycleService(),
    ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    val mViewModelStore = ViewModelStore()
    private var mFactory: ViewModelProvider.Factory? = null
    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                    mViewModelStore.clear()
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return mFactory ?: AndroidViewModelFactory(application).also {
            mFactory = it
        }
    }
}