package io.github.alaksion.sample

import androidx.lifecycle.ViewModel
import io.github.alaksion.UiStateHandler
import io.github.alaksion.UiStateOwner

internal data class SampleState(
    val name: String = "",
    val names: List<String> = listOf()
)

internal class StateViewModel : ViewModel() {

    private val privateState = UiStateHandler(SampleState())
    val publicState: UiStateOwner<SampleState> = privateState

    fun updateText(newValue: String) {
        privateState.updateState { updater ->
            updater.update { currentState ->
                currentState.copy(name = newValue)
            }
        }
    }

    fun submitName() {
        privateState.updateState {
            it.update { currentData ->
                currentData.copy(
                    name = "",
                    names = currentData.names + currentData.name
                )
            }
        }
    }

}