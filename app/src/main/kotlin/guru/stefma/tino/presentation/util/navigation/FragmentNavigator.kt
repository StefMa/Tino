package guru.stefma.tino.presentation.util.navigation

import androidx.fragment.app.Fragment

interface FragmentNavigator {
    fun showFragment(fragment: Fragment, tag: String? = null, addToBackStack: Boolean = true) {}
    fun hideFragment(fragment: Fragment? = null, tag: String? = null) {}
}

fun Fragment.showFragment(fragment: Fragment, tag: String? = null, addToBackStack: Boolean = true) =
    (requireActivity() as FragmentNavigator).showFragment(fragment, tag, addToBackStack)

fun Fragment.hideFragment(fragment: Fragment? = null, tag: String? = null) =
    (requireActivity() as FragmentNavigator).hideFragment(fragment, tag)