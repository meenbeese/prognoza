package hr.dtakac.prognoza.presentation.places

import hr.dtakac.prognoza.entities.Place
import hr.dtakac.prognoza.presentation.TextResource

data class PlacesState(
    val isLoading: Boolean = false,
    val empty: TextResource? = null,
    val places: List<PlaceUi> = listOf(),
    val selectedPlace: Place? = null
)

data class PlaceUi(
    val name: TextResource,
    val details: TextResource,
    val isSelected: Boolean
)