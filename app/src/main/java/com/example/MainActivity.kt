package com.example

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.databinding.ActivityMainBinding
import com.example.repository.FakeApiRepository
import com.example.ui.api.ApiListBottomSheet
import com.example.viewmodel.ApiListUiState
import com.example.viewmodel.ApiViewModel
import com.example.viewmodel.ApiViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Shared ApiViewModel using FakeApiRepository by default.
    // To connect your real Retrofit API, set ApiRepositoryProvider.repository = RealApiRepository()!
    private val viewModel: ApiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeDashboardStats()

        // Show the Network Inspector Bottom Sheet on first launch so the inspector UI is immediately visible
        if (savedInstanceState == null) {
            openInspector()
        }
    }

    private fun setupListeners() {
        binding.btnLaunchInspector.setOnClickListener {
            openInspector()
        }

        binding.fabInspect.setOnClickListener {
            openInspector()
        }

        binding.btnHostReloadData.setOnClickListener {
            viewModel.reloadSampleData()
        }
    }

    private fun openInspector() {
        val existing = supportFragmentManager.findFragmentByTag(ApiListBottomSheet.TAG)
        if (existing == null) {
            val sheet = ApiListBottomSheet.newInstance()
            sheet.show(supportFragmentManager, ApiListBottomSheet.TAG)
        }
    }

    private fun observeDashboardStats() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ApiListUiState.Success -> {
                            val requests = state.requests
                            val total = requests.size
                            val successCount = requests.count { it.isSuccess }
                            val errorCount = requests.count { it.isError }
                            val avgLatency = if (total > 0) requests.map { it.durationMs }.average().toInt() else 0
                            val successPct = if (total > 0) (successCount * 100) / total else 100

                            binding.tvStatTotal.text = total.toString()
                            binding.tvStatSuccessRate.text = "$successPct%"
                            binding.tvStatAvgDuration.text = "${avgLatency}ms"
                            binding.tvHostTrafficSummary.text = "$total requests recorded • $errorCount errors"
                        }
                        is ApiListUiState.Empty -> {
                            binding.tvStatTotal.text = "0"
                            binding.tvStatSuccessRate.text = "0%"
                            binding.tvStatAvgDuration.text = "0ms"
                            binding.tvHostTrafficSummary.text = "0 requests recorded"
                        }
                        else -> {
                            // Loading or Error
                        }
                    }
                }
            }
        }
    }
}
