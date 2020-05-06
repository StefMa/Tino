package guru.stefma.tino.presentation.statistics.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import guru.stefma.tino.R
import kotlinx.android.synthetic.main.fragment_app_statistics_bottom_sheet.*

class AppStatisticsBottomSheetFragment : Fragment() {

    private val adapter = AppStatisticsBottomSheetAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_statistics_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview.adapter = adapter
    }

    fun setItems(items: List<BottomSheetItems>) {
        adapter.listItems = items
    }

}

private class AppStatisticsBottomSheetAdapter :
    RecyclerView.Adapter<AppStatisticsBottomSheetAdapter.AppStatisticsBottomSheetViewHolder>() {

    var listItems: List<BottomSheetItems> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppStatisticsBottomSheetViewHolder =
        LayoutInflater.from(parent.context).run {
            val view = inflate(R.layout.item_app_statistics_bottom_sheet, null)
            AppStatisticsBottomSheetViewHolder(view)
        }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: AppStatisticsBottomSheetViewHolder, position: Int) {
        holder.bind(listItems[position])
    }

    private class AppStatisticsBottomSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView as CheckBox

        fun bind(item: BottomSheetItems) {
            with(checkbox) {
                text = item.appName
                setOnCheckedChangeListener(null)
                checkbox.isChecked = item.checked
                setOnCheckedChangeListener { _, isChecked -> item.onCheckedChanged(isChecked) }
            }
        }
    }
}

data class BottomSheetItems(
    val appName: String,
    val checked: Boolean,
    val onCheckedChanged: (Boolean) -> Unit
)