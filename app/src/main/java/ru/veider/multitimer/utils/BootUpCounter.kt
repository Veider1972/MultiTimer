package ru.veider.multitimer.utils

import android.content.Context

class BootUpCounter {
    companion object{
        private const val PREFERENCES = "Multitimer"
        private const val COUNTER = "Counter"
        private const val HAS_SET = "hasSet"
        fun getBootCounts(context: Context):Int{
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).run{
                if (getBoolean(HAS_SET,false)) return 11
                return getInt(COUNTER,1).apply{
                    edit()
                        .putInt(COUNTER,this+1)
                        .apply()
                }
            }
        }
        fun setMarked(context: Context){
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(HAS_SET,true)
                .apply()
        }
    }
}