package ru.veider.multitimer.domain.entity

import kotlinx.coroutines.flow.MutableStateFlow

interface Preferences {
    var keepScreenOn: MutableStateFlow<Boolean>
    var unlimitedNotification: MutableStateFlow<Boolean>
    var notificationLimits: MutableStateFlow<Int>
    var keptTime: MutableStateFlow<Int>
    var isKept: MutableStateFlow<Boolean>
    var sound: MutableStateFlow<Sound>
    var alarmChannelId: MutableStateFlow<String>
    var alarmChannelNum: MutableStateFlow<Int>
    var simpleChannelId: MutableStateFlow<String>
    var simpleChannelNum: MutableStateFlow<Int>
}