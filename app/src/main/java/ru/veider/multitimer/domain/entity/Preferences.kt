package ru.veider.multitimer.domain.entity

import kotlinx.coroutines.flow.MutableStateFlow

interface Preferences {
    val keepScreenOn: MutableStateFlow<Boolean>
    val unlimitedNotification: MutableStateFlow<Boolean>
    val notificationLimits: MutableStateFlow<Int>
    val keptTime: MutableStateFlow<Int>
    val isKept: MutableStateFlow<Boolean>
    val sound: MutableStateFlow<Sound>
    val alarmChannelId: MutableStateFlow<String>
    val alarmChannelNum: MutableStateFlow<Int>
    val simpleChannelId: MutableStateFlow<String>
    val simpleChannelNum: MutableStateFlow<Int>
    val timeEditorIsMulti: MutableStateFlow<Boolean>
}