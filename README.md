# Kotlin-Coroutines

NOTE : GIẢI PHAP THAY THẾ CHO EVENTBUS

class EventBus {
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()
    suspend fun emitEvent(event: String) {
        Log.d(TAG, "Emitting event = $event")
        _events.emit(event)
    }
    companion object {
        private const val TAG = "EventBus"
        val instance: EventBus by lazy { EventBus()
    }
  }

  private fun send(){
    eventBus.emitEvent(AppEvent.CONNECTION_RECONNECTED)
  }
  
  private fun receiver(){
    eventBus.events
    .filter { it == AppEvent.CONNECTION_RECONNECTED }
    .collectLatest { handleReconnection() }
  }
  
    
