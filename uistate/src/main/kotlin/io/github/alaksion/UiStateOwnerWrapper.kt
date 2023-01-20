package io.github.alaksion

import kotlinx.coroutines.flow.StateFlow

class UiStateOwnerWrapper<T>(
    initialData: T,
    initialState: UiStateType = UiStateType.Content
) : UiStateOwner<T> {

    private val mutableState = UiStateHandler(initialData, initialState)

    override val state: StateFlow<UiState<T>>
        get() = mutableState.state

    override val stateData: T
        get() {
            return state.value.stateData
        }

    protected fun blabla() {

    }


}