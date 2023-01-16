package io.github.alaksion.uievent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

/**
 * Base Contract of an UiEventOwner, a class that manages a queue of UiEvents and enables consumer
 * classes to collect and consume this events.
 * */
public interface UiEventOwner<T : UiEvent> {

    /**
     * StateFlow containing the current queue of UiEvents.
     * */
    val eventQueue: StateFlow<List<T>>

    /**
     * Adds an event to the UiEvent queue.
     * */
    fun sendEvent(event: T)

    /**
     * Consumes an event from the UiEvent queue, removing it from the current queue.
     * */
    suspend fun consumeEvent(event: T)

}

/***
 * Convenience function to collect the event queue StateFlow and execute a callback for every
 * event received. Events collected by this function will be automatically disposed through the call
 * of [UiEventOwner.consumeEvent].
 *
 * @param onEventReceived Callback to be executed when a new event is added to the queue
 */
suspend fun <T : UiEvent> UiEventOwner<T>.receiveEvents(
    onEventReceived: (T) -> Unit
) {
    eventQueue.collectLatest { queue ->
        queue.firstOrNull()?.let { event ->
            onEventReceived(event)
            consumeEvent(event)
        }
    }
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