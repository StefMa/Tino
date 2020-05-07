package guru.stefma.tino.presentation.util.viewmodel

import androidx.lifecycle.ViewModel

abstract class ViewModelHolder<P, T : ViewModel> : ViewModel() {
    private var cachedViewModel: T? = null

    fun get(params: P): T {
        return cachedViewModel ?: create(params).also { cachedViewModel = it }
    }

    protected abstract fun create(params: P): T
}