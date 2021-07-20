package hr.dtakac.prognoza.forecast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.dtakac.prognoza.R
import hr.dtakac.prognoza.databinding.CellForecastHourBinding
import hr.dtakac.prognoza.forecast.uimodel.HourUiModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class ForecastHoursRecyclerViewAdapter : RecyclerView.Adapter<ForecastHourViewHolder>() {
    var data: List<HourUiModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ForecastHourViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastHourViewHolder {
        val binding = CellForecastHourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastHourViewHolder(binding)
    }
}

class ForecastHourViewHolder(
    private val binding: CellForecastHourBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    fun bind(uiModel: HourUiModel) {
        val resources = binding.root.context.resources
        binding.tvTemperature.text = resources.getString(
            R.string.template_degrees,
            uiModel.temperature.roundToInt()
        )
        binding.tvPrecipitationAmount.apply {
            if (uiModel.precipitationAmount ?: 0f != 0f) {
                text = resources.getString(R.string.template_mm, uiModel.precipitationAmount)
                visibility = View.VISIBLE
            } else {
                text = null
                visibility = View.GONE
            }
        }
        binding.ivWeatherIcon.setImageResource(uiModel.weatherIcon.iconResourceId)
        binding.tvTime.text = uiModel.dateTimeGmt
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(dateTimeFormatter)
    }
}