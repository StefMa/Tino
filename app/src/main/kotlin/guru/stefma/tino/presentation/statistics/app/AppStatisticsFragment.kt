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
import guru.stefma.tino.presentation.util.labelForApplicationId
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

    private lateinit var appStatisticsAdapter: AppStatisticsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appStatisticsAdapter = AppStatisticsAdapter(this)
        viewPager.adapter = appStatisticsAdapter

        val viewModel = createAppStatisticsViewModel(uid)
        viewModel.bind()
    }

    private fun AppStatisticsViewModel.bind() {
        bind(applicationIds) {
            appStatisticsAdapter.appInfo = it
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = requireContext().labelForApplicationId(it[position].second)
            }.attach()
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

private typealias AppStatisticsInformation = Pair<String, String>
