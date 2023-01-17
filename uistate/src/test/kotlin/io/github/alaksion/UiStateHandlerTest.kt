package io.github.alaksion

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private data class SampleState(
    val name: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class UiStateHandlerTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var owner: UiStateOwner<SampleState>

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        owner = UiStateHandler(SampleState())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    @DisplayName("Initializing with State Data")
    fun `Should initialize with given initial data`() = runTest(dispatcher) {

        owner.state.test {
            val initialState = awaitItem()

            Assertions.assertEquals(initialState.stateData, SampleState())

            Assertions.assertEquals(initialState.stateType, UiStateType.Content)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    @DisplayName("Initializing with custom StateType")
    fun `Should initialize with customized initial state type`() = runTest {
        owner = UiStateHandler(SampleState(), UiStateType.Loading)

        owner.state.test {
            val initialState = awaitItem()

            Assertions.assertEquals(initialState.stateData, SampleState())

            Assertions.assertEquals(initialState.stateType, UiStateType.Loading)

            cancelAndIgnoreRemainingEvents()

        }
    }



}