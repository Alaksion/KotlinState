package br.com.alaksion.uievent

import java.util.UUID

abstract class UiEvent {
    val id: UUID = UUID.randomUUID()
}