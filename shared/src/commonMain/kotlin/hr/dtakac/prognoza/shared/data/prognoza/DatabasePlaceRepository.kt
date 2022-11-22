package hr.dtakac.prognoza.shared.data.prognoza

import hr.dtakac.prognoza.shared.domain.data.PlaceSaver
import hr.dtakac.prognoza.shared.domain.data.SavedPlaceGetter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import hr.dtakac.prognoza.shared.entity.Place as PlaceEntity

internal class DatabasePlaceRepository(
    private val placeQueries: PlaceQueries,
    private val ioDispatcher: CoroutineDispatcher,
    private val computationDispatcher: CoroutineDispatcher
) : SavedPlaceGetter, PlaceSaver {
    override suspend fun get(latitude: Double, longitude: Double): PlaceEntity? {
        return withContext(ioDispatcher) {
            placeQueries
                .get(latitude, longitude)
                .executeAsOneOrNull()
                ?.let {
                    PlaceEntity(
                        name = it.name,
                        details = it.details,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
        }
    }

    override suspend fun getAll(): List<PlaceEntity> {
        val placeDbModels = withContext(ioDispatcher) {
            placeQueries
                .getAll()
                .executeAsList()
        }
        return withContext(computationDispatcher) {
            placeDbModels
                .map {
                    PlaceEntity(
                        name = it.name,
                        details = it.details,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
        }
    }

    override suspend fun save(place: PlaceEntity) {
        withContext(ioDispatcher) {
            placeQueries.insert(
                Place(
                    latitude = place.latitude,
                    longitude = place.longitude,
                    name = place.name,
                    details = place.details
                )
            )
        }
    }
}