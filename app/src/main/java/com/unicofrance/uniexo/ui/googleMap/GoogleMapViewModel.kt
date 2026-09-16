package com.unicofrance.uniexo.ui.googleMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.data.repositories.ContainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoogleMapViewModel(
    private val containerRepository: ContainerRepository
) : ViewModel() {
    private val _locations = MutableStateFlow<List<LatLng>>(emptyList())
    val locations = _locations.asStateFlow()

    private var _containers : List<Container> = emptyList<Container>()

    private val _visibleArea = MutableStateFlow<LatLngBounds?>(null)

    private val _visiblePoint = MutableStateFlow<List<LatLng>>(emptyList())
    val visiblePoint : StateFlow<List<LatLng>> = _visiblePoint.asStateFlow()

    init {
        getContainers()
    }

    fun getContainers() {
        viewModelScope.launch {
            containerRepository
                .getAll()
                .map { containerList ->
                    _containers = containerList
                    containerList.map { container ->
                        LatLng(
                            container.latitude,
                            container.longitude
                        )
                    }
                }.collect {
                    _locations.value = it
                }
        }
    }

    fun getContainer(latLng: LatLng) : Container? {
        return _containers.firstOrNull { container ->
            LatLng(
                container.latitude,
                container.longitude
            ) == latLng
        }
    }

    fun getVisibleContainer(visibleArea : LatLngBounds?) {
        _visibleArea.value = visibleArea
        viewModelScope.launch {
            combine(_locations, _visibleArea) { locationPoints, area ->
                if (area == null) {
                    emptyList()
                } else {
                    locationPoints.filter { point ->
                        area.contains(point)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            ).collect {
                _visiblePoint.value = it
            }
        }
    }

}