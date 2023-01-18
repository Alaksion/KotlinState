package io.github.alaksion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface UiStateDataUpdater<T> {
    fun update(block: (T) -> T)
}

interface UiStateTypeUpdater {
    fun updateType(type: UiStateType)
}

class StateUpdater<T>(
    private val state: MutableStateFlow<UiState<T>>
) : UiStateDataUpdater<T>, UiStateTypeUpdater {

    override fun update(block: (T) -> T) {
        state.update { oldState ->
            oldState.copy(stateData = block(state.value.stateData))
        }
    }

    override fun updateType(type: UiStateType) {
        state.update { oldState ->
            oldState.copy(stateType = type)
        }
    }

}