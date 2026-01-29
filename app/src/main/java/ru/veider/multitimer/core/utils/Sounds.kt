package ru.veider.multitimer.core.utils

import android.content.ContentUris
import android.content.Context
import android.media.RingtoneManager
import android.media.RingtoneManager.TITLE_COLUMN_INDEX
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import ru.veider.multitimer.domain.entity.Sound
import java.io.File

//fun getAndroidMedia(context: Context): List<Sound> {
//    val mediaType = TYPE_NOTIFICATION
//    val ringtoneManager = RingtoneManager(context)
//    ringtoneManager.setType(mediaType)
//    val cursor = ringtoneManager.cursor
//    return (0 until cursor.count).map {
//        cursor.moveToPosition(it)
//        Sound(
//            title = cursor.getString(TITLE_COLUMN_INDEX),
//            uri = ringtoneManager.getRingtoneUri(it).toString()
//        )
//    }
//}

fun getAndroidMedia(context: Context): List<Sound> {
    val sounds = mutableListOf<Sound>()

    // Список всех возможных URI для поиска звуков
    val audioUris = mutableListOf<Uri>()
    audioUris.add(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL))
    // Общие колонки для запроса
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.MIME_TYPE
    )

    // Ищем аудиофайлы (не только уведомления)
    val selection = "${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%'"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    for (audioUri in audioUris) {
        try {
            context.contentResolver.query(
                audioUri,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    var title = cursor.getString(titleColumn)

                    // Если title пустой, используем display name
                    if (title.isNullOrEmpty()) {
                        title = cursor.getString(
                            cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                        )
                    }

                    // Создаем content URI
                    val uri = ContentUris.withAppendedId(audioUri, id)
                    sounds.add(Sound(title ?: "Без названия", uri.toString()))
                }
            }
        } catch (e: Exception) {
            // Пропускаем недоступные хранилища
            continue
        }
    }

    return sounds
}