package io.github.alaksion

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private data class SampleState(
    val name: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class UiStateHandlerTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var owner: MutableUiStateOwner<SampleState>

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

            assertThat(initialState.stateData).isEqualTo(SampleState())

            assertThat(initialState.stateType).isEqualTo(UiStateType.Content)

            cancelAndIgnoreRemainingEvents()

        }

    }

    @Test
    @DisplayName("Initializing with custom StateType")
    fun `Should initialize with customized initial state type`() = runTest {
        owner = UiStateHandler(SampleState(), UiStateType.Loading)

        owner.state.test {
            val initialState = awaitItem()

            assertThat(initialState.stateData).isEqualTo(SampleState())

            assertThat(initialState.stateType).isEqualTo(UiStateType.Loading)

            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    @DisplayName("Update state with updateState function")
    fun `Should update ui state synchronously`() = runTest(dispatcher) {

        owner.state.test {
            skipItems(1)

            owner.updateState { updater ->
                updater.update { currentData ->
                    currentData.copy(name = "new name")
                }
            }

            val currentState = awaitItem()

            assertThat(SampleState("new name")).isEqualTo(currentState.stateData)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("Update state with asyncUpdateState")
    fun `Should update ui state asynchronously`() = runTest(dispatcher) {

        owner.state.test {
            skipItems(1)

            owner.asyncUpdateState(showLoading = false) { stateUpdater ->
                stateUpdater.update { currentData ->
                    currentData.copy(name = "new name")
                }
            }

            val currentState = awaitItem()

            assertThat(SampleState("new name")).isEqualTo(currentState.stateData)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    @DisplayName("Update state with asyncUpdateState and show loading")
    fun `asyncUpdateState should show loading state when showLoading is set to true`() =
        runTest(dispatcher) {
            owner.state.test {
                skipItems(1)

                owner.asyncUpdateState(showLoading = true) { stateUpdater ->
                    stateUpdater.update { currentData ->
                        currentData.copy(name = "new name")
                    }
                }

                val loadingState = awaitItem()
                val currentState = awaitItem()
                val finalState = awaitItem()

                assertThat(SampleState("new name")).isEqualTo(currentState.stateData)

                assertThat(UiStateType.Content).isEqualTo(finalState.stateType)

                assertThat(UiStateType.Loading).isEqualTo(loadingState.stateType)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    @DisplayName("AsyncUpdate should show UiState.Error when unhandled exception is thrown")
    fun `Should set uiState to error when asyncUpdateState throws an unhandled exception`() =
        runTest(dispatcher) {
            owner.state.test {
                skipItems(1)

                owner.asyncUpdateState(showLoading = false) {
                    throw IllegalArgumentException()
                }

                val errorState = awaitItem()

                assertThat(errorState.stateType).isInstanceOf(UiStateType.Error::class.java)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    @DisplayName("UpdateState should show UiState.Error when unhandled exception is thrown")
    fun `Should set uiState to error when updateState throws an unhandled exception`() =
        runTest(dispatcher) {
            owner.state.test {
                skipItems(1)

                owner.updateState() {
                    throw IllegalArgumentException()
                }

                val errorState = awaitItem()

                assertThat(errorState.stateType).isInstanceOf(UiStateType.Error::class.java)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    @DisplayName("Async catching should set ui state to error when unhandled exception is thrown")
    fun `Should set UiStateType to error when asyncCatching throws an unhandled exception`() =
        runTest(dispatcher) {
            owner.state.test {
                skipItems(1)

                owner.asyncCatching(showLoading = false) {
                    throw IllegalArgumentException()
                }

                val errorState = awaitItem()

                assertThat(errorState.stateType).isInstanceOf(UiStateType.Error::class.java)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    @DisplayName("Async Catching should display loading state")
    fun `Should set UiStateType to error loading when asyncCatching is called with showLoading true`() =
        runTest(dispatcher) {
            owner.state.test {
                skipItems(1)

                owner.asyncCatching(showLoading = true) {

                }

                val loadginState = awaitItem()

                assertThat(loadginState.stateType).isEqualTo(UiStateType.Loading)

                cancelAndIgnoreRemainingEvents()
            }
        }

}
