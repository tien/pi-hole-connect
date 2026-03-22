package com.tien.piholeconnect.ui.screen.tools

import androidx.lifecycle.viewModelScope
import com.tien.piholeconnect.model.LoadState
import com.tien.piholeconnect.model.run
import com.tien.piholeconnect.repository.PiHoleRepositoryManager
import com.tien.piholeconnect.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
open class ToolsViewModel
@Inject
constructor(private val piHoleRepositoryManager: PiHoleRepositoryManager) : BaseViewModel() {

  enum class Tool {
    UPDATE_GRAVITY,
    RESTART_DNS,
    FLUSH_NETWORK_TABLE,
    FLUSH_LOG,
  }

  open val operationLoadState = MutableStateFlow<LoadState<Tool>>(LoadState.Idle())

  @OptIn(ExperimentalCoroutinesApi::class)
  open val gravityUpdatedAt =
      piHoleRepositoryManager.selectedPiHoleRepository
          .filterNotNull()
          .mapLatest {
            it.metricsApi.getMetricsSummary().body().gravity?.lastUpdate?.let { lastUpdate ->
              lastUpdate.toLong() * 1000
            }
          }
          .asViewFlowState()

  fun updateGravity() =
      performAction(Tool.UPDATE_GRAVITY) {
        piHoleRepositoryManager.getSelectedPiHoleRepository()?.actionsApi?.actionGravity()
      }

  fun restartDNS() =
      performAction(Tool.RESTART_DNS) {
        piHoleRepositoryManager.getSelectedPiHoleRepository()?.actionsApi?.actionRestartdns()
      }

  fun flushNetworkTable() =
      performAction(Tool.FLUSH_NETWORK_TABLE) {
        piHoleRepositoryManager.getSelectedPiHoleRepository()?.actionsApi?.actionFlusharp()
      }

  fun flushLog() =
      performAction(Tool.FLUSH_LOG) {
        piHoleRepositoryManager.getSelectedPiHoleRepository()?.actionsApi?.actionFlushlogs()
      }

  private fun performAction(tool: Tool, action: suspend () -> Unit) {
    operationLoadState.run(viewModelScope, data = tool) {
      coroutineScope {
        listOf(
                // The delay here seems to yield better UX
                async { delay(1000) },
                async { action() },
            )
            .forEach { it.await() }
      }

      if (tool == Tool.UPDATE_GRAVITY) {
        try {
          // Need to invoke any request as Pi-hole have an error
          // where the next response after gravity update will always be malformed
          piHoleRepositoryManager.getSelectedPiHoleRepository()?.ftlInformationApi?.getFtlinfo()
        } catch (_: Exception) {}

        // Pi-hole need some time to reflect gravity updates
        delay(1000)
        doRefresh()
      }

      tool
    }
  }
}
