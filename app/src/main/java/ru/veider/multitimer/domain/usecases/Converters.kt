package ru.veider.multitimer.domain.usecases

import ru.veider.multitimer.const.CounterState
import ru.veider.multitimer.const.PRIMARY_KEY
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.repository.CounterEntity

fun CounterEntity.toCounter(): Counter = Counter(
    id = id,
    currentProgress = currentProgress,
    maxProgress = maxProgress,
    startTime = startTime,
    state = enumValues<CounterState>()[state],
    title = title
)

fun Counter.toCounterEntity(): CounterEntity = CounterEntity(
    key = PRIMARY_KEY,
    id = id,
    currentProgress = currentProgress,
    maxProgress = maxProgress,
    startTime = startTime,
    state = state.ordinal,
    title = title
)