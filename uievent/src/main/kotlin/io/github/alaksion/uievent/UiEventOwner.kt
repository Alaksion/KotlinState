package io.github.alaksion.uievent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public interface UiEventOwner<T : UiEvent> {

    val eventQueue: StateFlow<List<T>>

    fun sendEvent(event: T)

    suspend fun consumeEvent(event: T)

}

public class UiEventHandler<T : UiEvent> : UiEventOwner<T> {

    private val mutableEventQueue = MutableStateFlow(listOf<T>())

    override val eventQueue: StateFlow<List<T>> = mutableEventQueue.asStateFlow()

    override suspend fun consumeEvent(event: T) {
        mutableEventQueue.update { oldSnapshot ->
            oldSnapshot.filter { currentEvent ->
                currentEvent.id != event.id
            }
        }
    }

    override fun sendEvent(event: T) {
        mutableEventQueue.update { oldSnapshot ->
            oldSnapshot + event
        }
    }

}