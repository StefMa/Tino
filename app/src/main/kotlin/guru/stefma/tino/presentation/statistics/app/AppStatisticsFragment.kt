package guru.stefma.tino.presentation.statistics.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import guru.stefma.tino.R
import guru.stefma.tino.presentation.statistics.app.single.createSingleAppStatisticsFragment
import guru.stefma.tino.presentation.util.toAppName
import guru.stefma.tino.presentation.util.viewbinding.bind
import kotlinx.android.synthetic.main.fragment_app_statistics.*

private const val EXTRA_UID = "uid"

fun createAppStatisticsFragment(uid: String): AppStatisticsFragment =
    AppStatisticsFragment().apply {
        arguments = Bundle().apply {
            putString(EXTRA_UID, uid)
        }
    }

class AppStatisticsFragment : Fragment() {

    private val uid by lazy {
        arguments?.getString(EXTRA_UID) ?: throw IllegalAccessException(
            "You have to create an instance of the AppStatisticsFragment via 'createAppStatisticsFragment'"
        )
    }

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = createAppStatisticsViewModel(uid)
        viewModel.bind()
    }

    private fun AppStatisticsViewModel.bind() {
        bind(appStatisticsInfo) {
            tabLayoutMediator?.detach()
            val appStatisticsAdapter = AppStatisticsAdapter(this@AppStatisticsFragment)
            viewPager.adapter = appStatisticsAdapter
            appStatisticsAdapter.appInfo = it

            val appNames = it.map { it.appId.toAppName(requireContext()) }.sorted()
            TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = appNames[position] }.also {
                tabLayoutMediator = it
            }.attach()
        }
        bind(filterItems) {
            childFragmentManager.findFragmentById(R.id.bottomSheet).apply {
                (this as AppStatisticsBottomSheetFragment)
                val bottomSheetItems = it.map {
                    BottomSheetItems(
                        appName = it.appId.toAppName(requireContext()),
                        checked = it.checked,
                        onCheckedChanged = it.onCheckedChanged
                    )
                }.sortedBy { it.appName }
                setItems(bottomSheetItems)
            }
        }
    }

}

private class AppStatisticsAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    var appInfo: List<AppStatisticsInformation> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = appInfo.size

    override fun createFragment(position: Int): Fragment {
        val (uid, appId) = appInfo[position]
        return createSingleAppStatisticsFragment(uid = uid, appId = appId)
    }
}
