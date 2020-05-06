package guru.stefma.tino.presentation.util.viewmodel

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import guru.stefma.tino.dependencyGraph

inline fun <reified T : ViewModel> ComponentActivity.getViewModel(): T =
    ViewModelProvider(viewModelStore, dependencyGraph.viewModelFactory).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
    ViewModelProvider(viewModelStore, dependencyGraph.viewModelFactory).get(T::class.java)