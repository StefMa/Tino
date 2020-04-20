package guru.stefma.tino.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.presentation.main.MainFragment
import guru.stefma.tino.presentation.splashscreen.SplashscreenFragment
import guru.stefma.tino.presentation.util.navigation.FragmentNavigator
import guru.stefma.tino.presentation.util.viewbinding.bind

class MainActivity : AppCompatActivity(), FragmentNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = createMainViewModel()
        viewModel.bind()
    }

    private fun MainViewModel.bind() {
        bind(showSplashscreen) {
            if (it) {
                showFragment(SplashscreenFragment(), "splashscreenTag", false)
            } else {
                hideFragment(tag = "splashscreenTag")
            }
        }
        bind(showMain) {
            if (it) {
                showFragment(MainFragment(), "mainTag", false)
            } else {
                hideFragment(tag = "mainTag")
            }
        }
    }

    override fun showFragment(fragment: Fragment, tag: String?, addToBackStack: Boolean): Unit =
        with(supportFragmentManager) {
            if (tag != null && findFragmentByTag(tag) != null) return@with // Don't add the fragment if its already added
            beginTransaction()
                .replace(R.id.container, fragment, tag)
                .apply { if (addToBackStack) addToBackStack(fragment::class.java.simpleName) }
                .commit()
        }

    override fun hideFragment(fragment: Fragment?, tag: String?): Unit =
        with(supportFragmentManager) {
            val fragmentToRemove = fragment ?: findFragmentByTag(tag)
            fragmentToRemove?.let {
                beginTransaction()
                    .remove(it)
                    .commit()
            }
        }

}