package com.unicofrance.uniexo.ui.googleMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.data.repositories.ContainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GoogleMapViewModel(
    private val containerRepository: ContainerRepository
) : ViewModel() {
    private val _locations = MutableStateFlow<List<LatLng>>(emptyList())
    val locations = _locations.asStateFlow()

    private var _containers : List<Container> = emptyList<Container>()

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

}