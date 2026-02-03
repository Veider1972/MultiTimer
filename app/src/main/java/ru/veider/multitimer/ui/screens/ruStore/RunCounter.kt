package ru.veider.multitimer.ui.screens.ruStore

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import ru.rustore.sdk.core.tasks.OnFailureListener
import ru.rustore.sdk.core.tasks.OnSuccessListener
import ru.rustore.sdk.review.RuStoreReviewManagerFactory
import ru.rustore.sdk.review.model.ReviewInfo
import ru.veider.multitimer.App
import ru.veider.multitimer.MainActivity
import ru.veider.multitimer.domain.entity.Preferences
import ru.veider.multitimer.utils.BootUpCounter

@Composable
fun RunCounter() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs: Preferences = koinInject()
    val hasFeedback by prefs.hasFeedback.collectAsState()

    LaunchedEffect(hasFeedback) {
        if (!hasFeedback){
            scope.launch(Dispatchers.IO) {
                Log.d("RuStore", "Старт запроса")
                RuStoreReviewManagerFactory.create(context as MainActivity).let { manager ->
                    Log.d("RuStore", "Менеджер создан")
                    manager.requestReviewFlow()
                        .addOnSuccessListener { reviewInfo ->
                            Log.d("RuStore", "Запрос подготовлен: $reviewInfo")
                            scope.launch(Dispatchers.IO) {
                                prefs.runCounter.collect { counter ->
                                    if (counter == 30L || counter % 30 == 0L )
                                    manager.launchReviewFlow(reviewInfo)
                                        .addOnSuccessListener {
                                            Log.d("RuStore", "Отзыв отправлен")
                                            prefs.hasFeedback.value = true
                                        }
                                        .addOnFailureListener { throwable ->
                                            Log.d("RuStore", "Отзыв не отправлен: $throwable")
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { throwable ->
                            Log.d("RuStore", "Ошибка подготовки запроса: $throwable")
                        }
                }
        }

      }
    }
}