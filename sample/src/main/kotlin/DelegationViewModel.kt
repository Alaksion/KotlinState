package io.github.alaksion.sample

import androidx.lifecycle.ViewModel
import io.github.alaksion.MutableUiStateOwner
import io.github.alaksion.UiStateHandler

internal data class SampleState(
    val name: String = "",
    val names: List<String> = listOf()
)

internal class StateViewModel :
    ViewModel(),
    MutableUiStateOwner<SampleState> by UiStateHandler(SampleState()) {
    fun updateText(newValue: String) {
        updateState { updater ->
            updater.update { currentState ->
                currentState.copy(name = newValue)
            }
        }
    }

    fun submitName() {
        updateState {
            it.update { currentData ->
                currentData.copy(
                    name = "",
                    names = currentData.names + currentData.name
                )
            }
        }
    }

}