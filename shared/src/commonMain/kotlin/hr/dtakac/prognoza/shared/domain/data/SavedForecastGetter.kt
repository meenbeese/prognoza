package hr.dtakac.prognoza.shared.domain.data

import hr.dtakac.prognoza.shared.entity.ForecastDatum

internal interface SavedForecastGetter {
    suspend fun get(
        latitude: Double,
        longitude: Double
    ): List<ForecastDatum>
}