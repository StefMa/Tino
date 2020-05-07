package guru.stefma.tino.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import guru.stefma.tino.R
import guru.stefma.tino.presentation.statistics.createStatisticsFragment
import guru.stefma.tino.presentation.util.hideKeyboard
import guru.stefma.tino.presentation.util.navigation.showFragment
import guru.stefma.tino.presentation.util.showSnackbar
import guru.stefma.tino.presentation.util.viewbinding.bind
import guru.stefma.tino.presentation.util.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = getViewModel<AccountViewModel>()
        viewModel.bind()
        saveUserName.setOnClickListener {
            requireView().hideKeyboard()
            viewModel.saveUseName(accountUserName.text.toString())
        }
        seeStatisticsFromButton.setOnClickListener {
            requireView().hideKeyboard()
            viewModel.seeStatisticsFrom(seeStatisticsFrom.text.toString())
        }
    }

    private fun AccountViewModel.bind() {
        bind(username) {
            accountUserName.setText(it)
        }
        bind(showUserStatistics) {
            showFragment(createStatisticsFragment(it))
        }
        bind(showUserNameSaveFailed) {
            requireView().showSnackbar(getString(R.string.account_save_username_error))
        }
        bind(showUserNameDoesNotExistError) {
            requireView().showSnackbar(getString(R.string.account_user_not_exist, it))
        }
    }

}