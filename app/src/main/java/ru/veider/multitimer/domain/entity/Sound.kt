package ru.veider.multitimer.domain.entity

import android.net.Uri
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
data class Sound(
    @SerializedName("title") val title: String,
    @SerializedName("uri") val uri: String)
