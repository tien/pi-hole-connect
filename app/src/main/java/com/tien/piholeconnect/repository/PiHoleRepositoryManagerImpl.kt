package com.tien.piholeconnect.repository

import androidx.datastore.core.DataStore
import com.tien.piholeconnect.model.PiHoleConnections
import com.tien.piholeconnect.util.getSelectedConnection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class PiHoleRepositoryManagerImpl
@Inject
constructor(
    private val piHoleRepositoryFactory: PiHoleRepository.Factory,
    private val piHoleConnectionsDataStore: DataStore<PiHoleConnections>,
) : PiHoleRepositoryManager {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val selectedPiHoleRepository =
        piHoleConnectionsDataStore.data
            .map {
                it.getSelectedConnection()?.let { (id, connection) ->
                    id to connection.configuration
                }
            }
            .distinctUntilChanged()
            .map { it?.let { piHoleRepositoryFactory.create(it.first, it.second) } }
            .stateIn(
                scope = MainScope(),
                started = SharingStarted.WhileSubscribed(5_000, 0),
                initialValue = null,
            )
            .mapLatest { it?.authenticate() }

    override suspend fun getSelectedPiHoleRepository(): PiHoleRepository? {
        return selectedPiHoleRepository.firstOrNull()
    }

    override suspend fun setSelectedPiHole(id: String) {
        piHoleConnectionsDataStore.updateData {
            require(it.containsConnections(id))
            it.toBuilder().setSelectedConnectionId(id).build()
        }
    }
}
