package hr.dtakac.prognoza.database.dao

import androidx.room.*
import hr.dtakac.prognoza.database.ForecastHourDateTimeConverter
import hr.dtakac.prognoza.database.entity.ForecastHour
import java.time.ZonedDateTime

@Dao
interface ForecastHourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(forecastHours: List<ForecastHour>)

    @Query("DELETE FROM ForecastHour WHERE DATE(time) < DATE('now')")
    suspend fun deletePastForecastHours()

    @Query(
        value = """
            SELECT * FROM ForecastHour 
            WHERE DATETIME(time) BETWEEN DATETIME(:start) AND DATETIME(:end) 
            AND locationId == :locationId 
            ORDER BY DATETIME(time) ASC
        """
    )
    @TypeConverters(ForecastHourDateTimeConverter::class)
    suspend fun getForecastHours(start: ZonedDateTime, end: ZonedDateTime, locationId: Long): List<ForecastHour>
}