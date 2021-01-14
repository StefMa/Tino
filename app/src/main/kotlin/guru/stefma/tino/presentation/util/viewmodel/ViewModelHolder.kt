package guru.stefma.tino.presentation.util.viewmodel

abstract class ViewModelHolder<P, T : DisposableViewModel> : DisposableViewModel() {
    private var cachedViewModel: T? = null

    fun get(params: P): T {
        return cachedViewModel ?: create(params).also { cachedViewModel = it }
    }

    protected abstract fun create(params: P): T

    override fun onCleared() {
        super.onCleared()
        cachedViewModel?.disposables?.clear()
    }
}