# KotlinState
Kotlin library designed to create a state management structure.


## Download

```kotlin


// Add to your build.gradle.kts file
dependencies {
    val latestVersion = "1.0.0-alpha-05"

    implementation("io.github.alaksion:ui-state:$latestVersion")
    implementation("io.github.alaksion:ui-event:$latestVersion")
}

```

# Usage
KotlinState is composed by two different API's to handle user interaction. `UiState` serves as a state class for views (usually a data class), and `UiEvent` handles one shot events from a presenter to the consumer view.

## UiState
UiState is a `StateFlow<UiState<T<>` which holds a data class with 2 properties: `T` and `UiStateType`. There are 2 recommended ways to use this class: using it as a class member and using it through delegation implementation.

### Using UiState as class member
```kotlin
internal class UiPresenter() {
    
    /**
    *  MutableUiStateOwner interface provides access to methods responsible for updating the state while UiStateOwner only
    *  allows access to a read only StateFlow<UiState<T>. Ideally the view will only need direct access to the current snapshot of UiState, that's
    *  why you shouldn't expose a MutableUiStateOwner unless is strictly necessary.
    */ 
    private val mutableState: MutableUiStateOwner<SampleState> = UiStateHandler<SampleState>(SampleState())
    val readOnlyState: UiStateOwner<SampleState> = mutableState
    
    // Example of public function that wraps the update state behavior.
    fun updateSomething(value: String) {
        mutableState.updateState { updater ->
            updater.update { currentData -> 
                currentData.copy(someString = value)
            }
        }
    }
    
}

```

### Using Ui State through implementation delegation
It's also possible to transform your Presenter in a MutableUiStateOwner<T> using delegation implementation since it's an interface and UiStateHandler is an implementation of this interface. Be aware that doing this will make the updateState methods visible for you view.

```kotlin

internal class UiPresenter: MutableUiStateOwner<SampleState> by UiStateHandler<SampleState>(SampleState) {

    /** Since the MutableUiStateOwner interface implementation is delegated, state update methods can be accessed directly, without the need of explicit declaration
    * of the UiState
    */
    
    fun updateSomething(value: String) {
        updateState { updater -> 
            updater.update {currentState -> 
                currentState.copy(someString = value)
            }
        }
    }

}

```

## Ui Event
UiEvent is a strucute designed to handle one shot events sent from the presenter to the view. In most cases these events are navigating to another screen when a request succeeds or displaying some kind of temporary error indication (like toast messages or snackbars).

Like `UiState`, `UiEvent` can also be used as a class member or through delegation implementation.

### Using Ui Event as a class member

```kotlin

internal sealed class SampleEvent: UiEvent {
    object TestEvent: SampleEvent()
}


internal class UiPresenter: MutableUiStateOwner<SampleState> by UiStateHandler<SampleState>(SampleState) {

    /** 
        Ui Events and the logic to consume these events are stored in the `UiEventOwner` interface while the logic to emit these events is parte of the `UiEventSender`  interface.
        This is a similar design to `UiStateOwner` and `MutableUiStateOwner`, so usually the presenter should only expose the UiEventOwner instance.
    */
    
    private val mutableEventStream: UiEventOwnerSender<SampleEvent> = UiEventHandler()
    val eventStream: UiEventOwner<SampleEvent> = mutableEventStream
    
    // Sample public wrapper to send events
    fun sendEvent() {
        mutableEventStream.sendEvent(SampleEvent.TestEvent)
    }

}

```

### Using UiEvent through implementation delegation

```kotlin

internal class SamplePresenter() : UiEventOwnerSender<CreateAlbumEvents> by UiEventHandler() {

    fun sendEvent() {
        sendEvent(SampleEvent.TestEvent)
    }

}

```

#### Consuming Ui Events
The UiEvent queue stores events in a queue for them to be consumed sequentially (FIFO). This API works by adding an event to the queue and only when `UiEventOwner.consumeEvent` is called the current event will be properly dismissed. This happens to guarantee that every event in the queue, and to achieve this behavior, events must be manually assigned as consumed so they can be safely removed from the queue.

`UiEventOwner` owns a convenience method to collect emmited events, process them, and automatically dispose them through a `consumeEvent` call. Use `UiEventOwner.receiveEvents` in your view class to use this default behavior. If custom behavior is needed before declaring an event as consumed both the event queue and the consumeEvent methods have public visibility.

```kotlin
// Compose example using UiEventOwner as class member

@Composable
fun MyScreen() {
    val presenter<SamplePresenter> = ...
    
    LaunchedEffect(key1 = presenter) {
       presenter.receiveEvents { event ->
            print("Event received")
       }
    }
   
}

```
    
## Testing
Both UiState and UiEvent behaviours are wrappend inside a StateFlow so testing these structures is nothing but a regular StateFlow test. Even though it's very simple to write tests for these structures keep in mind the following things in the next topic that might lead to errors.

StateFlow testing can be done manually but i recommned using [Cash App Turbine](https://github.com/cashapp/turbine) for convenience.
    
### Ui State Testing
When writing tests for Ui State notice that functions like `asyncUpdateState`, `updateState`, and `asyncCatching` might emit different amount of values depending on the parameters you set. Functions with `async` prefixes have a parameter named `isLoading`, when this parameter is set to true there will be an extra emission to UiState (to set the loading state). 
    
Also notice that `UiState` update functions will always update UiStateType to `Content` at the end of the execution block, this will also cause an extra emission.
    
### Ui Event Testing
Ui event doesn't launch any emissions internally except for the expected ones (adding events and consuming them).


