package guru.stefma.tino.presentation.statistics.app.single

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

private const val EXTRA_UID = "uid"
private const val EXTRA_APP_ID = "appId"
private typealias ApplicationId = String

fun createSingleAppStatisticsFragment(
    uid: String,
    appId: ApplicationId
): SingleAppStatisticsFragment =
    SingleAppStatisticsFragment().apply {
        arguments = Bundle().apply {
            putString(EXTRA_UID, uid)
            putString(EXTRA_APP_ID, appId)
        }
    }

class SingleAppStatisticsFragment : Fragment() {

    private val uid by lazy {
        arguments?.getString(EXTRA_UID) ?: throw IllegalAccessException(
            "You have to create an instance of the SingleAppStatisticsFragment via 'createSingleAppStatisticsFragment'"
        )
    }

    private val appId: ApplicationId by lazy {
        arguments?.getString(EXTRA_APP_ID) ?: throw IllegalAccessException(
            "You have to create an instance of the SingleAppStatisticsFragment via 'createSingleAppStatisticsFragment'"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(inflater.context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view as TextView).text = "// TODO"
    }
}