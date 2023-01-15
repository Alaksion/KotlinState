package br.com.alaksion.uistate

public sealed class UiStateType {
    object Content : UiStateType()
    object Loading : UiStateType()
    data class Error(val error: Throwable) : UiStateType()
}

public data class UiState<T>(
    val stateData: T,
    val stateType: UiStateType
)
