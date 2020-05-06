package guru.stefma.tino.presentation.util.viewmodel

import androidx.lifecycle.ViewModel

//TODO: Test ProGuard and update Proguard file!!!

abstract class ViewModelHolder<P, T : ViewModel> : ViewModel() {
    abstract fun get(params: P): T
}