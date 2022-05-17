# Kotlin-Coroutines

NOTE : GIẢI PHAP THAY THẾ CHO EVENTBUS
==================================================
class EventBus {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()

    suspend fun emitEvent(event: AppEvent) {
        Log.d(TAG, "Emitting event = $event")
        _events.emit(event)
    }

    companion object {
        private const val TAG = "EventBus"
    }
}
  
  ============================================
  eventBus.emitEvent(AppEvent.CONNECTION_RECONNECTED)
  ============================================
  eventBus.events
    .filter { it == AppEvent.CONNECTION_RECONNECTED }
    .collectLatest { handleReconnection() }
