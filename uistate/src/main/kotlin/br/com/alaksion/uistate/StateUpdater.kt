package br.com.alaksion.uistate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class StateUpdater<T>(
    private val state: MutableStateFlow<UiState<T>>
) {

    fun updateState(block: (T) -> T) {
        state.update { oldState ->
            oldState.copy(stateData = block(state.value.stateData))
        }
    }

    fun updateStateType(type: UiStateType) {
        state.update { oldState ->
            oldState.copy(stateType = type)
        }
    }

}