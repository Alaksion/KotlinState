package io.github.alaksion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * Interface representing the base contract of an [UiStateOwner]. Can be used alongside with
 * [UiStateHandler] to enable the usage of delegation pattern.
 * */
public interface UiStateOwner<T> {

    /**
     * StateFlow containing data about the current snapshot of the UiState. Use [UiState.stateData]
     * to read the state data itself, and use [UiState.stateType] to keep track of the current state
     * display type.
     * */
    val state: StateFlow<UiState<T>>

    /**
     * Provides a block with access to [StateUpdater] to update the state synchronously. Any
     * unhandled exceptions will automatically set [UiState.stateType] to [UiStateType.Error].
     *
     * @param block Function to be evaluated by [StateUpdater] so the data can be updated.
     * */
    fun updateState(block: (StateUpdater<T>) -> T)

    /**
     * Convenience access to data <T> of the [UiState] class. Used when the [UiStateType] is not
     * relevant to the consumer class.
     * */
    val peekState: StateFlow<T>

    /**
     * Provides a block with access to [StateUpdater] to update the state with support to suspend
     * functions. Any unhandled exceptions will automatically set [UiState.stateType]
     * to [UiStateType.Error].
     *
     * @param block [UiStateHandler] block responsible for calling methods related to state updates.
     * @param showLoading Whether or not the loading state should be displayed during the execution
     * of the block param. True by default.
     * */
    suspend fun asyncUpdateState(
        block: suspend (StateUpdater<T>) -> T,
        showLoading: Boolean = true,
    )

    /**
     * Provides a block to run suspend functions that won't update the UiState. Any unhandled
     * exceptions will automatically update [UiState.stateType] to [UiStateType.Error]
     *
     * @param block [UiStateHandler] block responsible for calling suspend methods.
     * @param showLoading Whether or not the loading state should be displayed during the execution
     * of the block param. True by default.
     * */
    suspend fun asyncCatching(
        block: suspend () -> Unit,
        showLoading: Boolean = true
    )

}

public class UiStateHandler<T>(
    initialData: T,
    initialType: UiStateType = UiStateType.Content,
) : UiStateOwner<T> {

    private val mutableState = MutableStateFlow(
        UiState(
            stateData = initialData,
            stateType = initialType
        )
    )

    private val updater by lazy {
        StateUpdater(mutableState)
    }

    override val state: StateFlow<UiState<T>> = mutableState.asStateFlow()

    override val peekState: StateFlow<T> = mutableState
        .mapLatest { uiState -> uiState.stateData }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(),
            initialValue = initialData
        )

    override suspend fun asyncCatching(block: suspend () -> Unit, showLoading: Boolean) {
        kotlin.runCatching {
            if (showLoading) updater.updateStateType(UiStateType.Loading)
            block()
            updater.updateStateType(UiStateType.Content)
        }.onFailure { exception ->
            updater.updateStateType(UiStateType.Error(exception))
        }
    }

    override suspend fun asyncUpdateState(
        block: suspend (StateUpdater<T>) -> T,
        showLoading: Boolean
    ) {
        kotlin.runCatching {
            if (showLoading) updater.updateStateType(UiStateType.Loading)
            block(updater)
            updater.updateStateType(UiStateType.Content)
        }.onFailure { exception ->
            updater.updateStateType(UiStateType.Error(exception))
        }
    }

    override fun updateState(block: (StateUpdater<T>) -> T) {
        kotlin.runCatching {
            block(updater)
            updater.updateStateType(UiStateType.Content)
        }.onFailure { exception ->
            updater.updateStateType(UiStateType.Error(exception))
        }
    }

}