package io.github.alaksion.uievent

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private sealed class SampleEvents : UiEvent() {
    object TestEvent : SampleEvents()

}


@ExperimentalCoroutinesApi
internal class UiEventHandlerTest {

    private lateinit var handler: UiEventHandler<SampleEvents>
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        handler = UiEventHandler()
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should start with empty event queue`() = runTest(dispatcher) {

        handler.eventQueue.test {
            val snapshot = awaitItem()

            Assertions.assertThat(snapshot).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `Should add event to queue`() = runTest(dispatcher) {

        handler.eventQueue.test {
            skipItems(1)

            handler.sendEvent(SampleEvents.TestEvent)
            val snapshot = awaitItem()

            Assertions.assertThat(snapshot).isEqualTo(listOf(SampleEvents.TestEvent))

            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `Should consume event from queue`() = runTest {
        val event = SampleEvents.TestEvent

        handler.sendEvent(event)
        handler.consumeEvent(event)

        advanceUntilIdle()

        handler.eventQueue.test {

            val snapshot = expectMostRecentItem()
            Assertions.assertThat(snapshot).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

}