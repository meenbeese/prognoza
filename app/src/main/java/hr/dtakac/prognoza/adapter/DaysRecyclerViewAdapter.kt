package hr.dtakac.prognoza.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.dtakac.prognoza.R
import hr.dtakac.prognoza.databinding.CellDayBinding
import hr.dtakac.prognoza.extensions.*
import hr.dtakac.prognoza.uimodel.cell.DayUiModel

class DaysRecyclerViewAdapter : ListAdapter<DayUiModel, DayViewHolder>(DayDiffCallback()) {
    private val onItemClickCallback = object : (Int) -> Unit {
        override fun invoke(position: Int) {
            val itemAtPosition = getItem(position)
            itemAtPosition.isExpanded = !itemAtPosition.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = CellDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding, onItemClickCallback)
    }
}

class DayViewHolder(
    private val binding: CellDayBinding,
    onItemClickCallback: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.clHeader.setOnClickListener { onItemClickCallback.invoke(adapterPosition) }
    }

    fun bind(uiModel: DayUiModel) {
        binding.apply {
            val context = root.context
            tvDateTime.text = if (DateUtils.isToday(uiModel.time.toInstant().toEpochMilli())) {
                context.getString(R.string.today)
            } else {
                DateUtils.formatDateTime(
                    root.context,
                    uiModel.time.toInstant().toEpochMilli(),
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_WEEKDAY
                )
            }
            tvTemperatureHigh.text =
                context.formatTemperatureValue(
                    uiModel.highTemperature,
                    uiModel.displayDataInUnit
                )
            tvTemperatureLow.text =
                context.formatTemperatureValue(
                    uiModel.lowTemperature,
                    uiModel.displayDataInUnit
                )
            tvDescription.text =
                context.formatRepresentativeWeatherIconDescription(uiModel.representativeWeatherDescription)
            ivWeatherIcon.setImageResource(
                uiModel.representativeWeatherDescription?.weatherDescription?.iconResourceId
                    ?: R.drawable.ic_cloud
            )
            tvPrecipitation.text =
                root.context.formatTotalPrecipitation(
                    uiModel.totalPrecipitationAmount,
                    uiModel.displayDataInUnit
                )
            tvWind.text = context.formatWindWithDirection(
                uiModel.maxWindSpeed,
                uiModel.windFromCompassDirection,
                uiModel.displayDataInUnit
            )
            tvHumidity.text = context.formatHumidityValue(uiModel.maxHumidity)
            tvPressure.text =
                context.formatPressureValue(uiModel.maxPressure, uiModel.displayDataInUnit)
            clDetails.visibility = if (uiModel.isExpanded) View.VISIBLE else View.GONE
        }
    }
}

class DayDiffCallback : DiffUtil.ItemCallback<DayUiModel>() {
    override fun areContentsTheSame(oldItem: DayUiModel, newItem: DayUiModel): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: DayUiModel, newItem: DayUiModel): Boolean {
        return oldItem.id == newItem.id
    }
}