package br.com.alaksion.uistate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

public interface UiStateOwner<T> {

    val state: StateFlow<UiState<T>>

    fun updateState(block: (StateUpdater<T>) -> T)

    fun peekState(): StateFlow<T>

    suspend fun asyncUpdateState(
        block: suspend (StateUpdater<T>) -> T,
        showLoading: Boolean = true,
    )

    suspend fun asyncCatching(
        block: suspend () -> Unit,
        showLoading: Boolean = true
    )

}

public class UiStateHandler<T>(
    initialData: T,
    initialType: UiStateType = UiStateType.Content
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

    override fun peekState(): StateFlow<T> {
        return MutableStateFlow(mutableState.value.stateData)
    }

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