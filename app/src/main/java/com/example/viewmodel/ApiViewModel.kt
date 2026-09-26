package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.ApiFilter
import com.example.model.ApiRequest
import com.example.repository.ApiRepository
import com.example.repository.FakeApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ApiListUiState {
    data object Loading : ApiListUiState
    data class Success(val requests: List<ApiRequest>) : ApiListUiState
    data class Error(val message: String) : ApiListUiState
    data object Empty : ApiListUiState
}

// ============================================================================
// REPOSITORY PROVIDER
// Allows setting the default repository (e.g. RealApiRepository) app-wide
// ============================================================================
object ApiRepositoryProvider {
    var repository: ApiRepository = FakeApiRepository()
}

class ApiViewModel @JvmOverloads constructor(
    private val repository: ApiRepository = ApiRepositoryProvider.repository
) : ViewModel() {

    private val _rawRequests = MutableStateFlow<List<ApiRequest>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(ApiFilter())
    val filter: StateFlow<ApiFilter> = _filter.asStateFlow()

    private val _selectedRequest = MutableStateFlow<ApiRequest?>(null)
    val selectedRequest: StateFlow<ApiRequest?> = _selectedRequest.asStateFlow()

    // Combined filtered requests stream
    private val _filteredRequests = combine(
        _rawRequests,
        _searchQuery,
        _filter
    ) { requests, query, currentFilter ->
        filterAndSearch(requests, query, currentFilter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val uiState: StateFlow<ApiListUiState> = combine(
        _isLoading,
        _errorMessage,
        _filteredRequests,
        _rawRequests
    ) { loading, error, filtered, raw ->
        when {
            loading -> ApiListUiState.Loading
            error != null -> ApiListUiState.Error(error)
            raw.isEmpty() || filtered.isEmpty() -> ApiListUiState.Empty
            else -> ApiListUiState.Success(filtered)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApiListUiState.Loading
    )

    val totalCount: StateFlow<Int> = combine(_rawRequests) { it.first().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredCount: StateFlow<Int> = combine(_filteredRequests) { it.first().size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val data = repository.getRequests()
                _rawRequests.value = data
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load network requests"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(newFilter: ApiFilter) {
        _filter.value = newFilter
    }

    fun clearFilters() {
        _filter.value = ApiFilter()
        _searchQuery.value = ""
    }

    fun selectRequest(request: ApiRequest?) {
        _selectedRequest.value = request
    }

    fun clearHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.clearHistory()
                _rawRequests.value = emptyList()
                _selectedRequest.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to clear history: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reloadSampleData() {
        if (repository is FakeApiRepository) {
            repository.reloadInitialData()
        }
        loadRequests()
    }

    private fun filterAndSearch(
        requests: List<ApiRequest>,
        query: String,
        filter: ApiFilter
    ): List<ApiRequest> {
        val trimmedQuery = query.trim()

        return requests.filter { req ->
            // 1. Filter by Methods
            if (filter.methods.isNotEmpty() && req.method !in filter.methods) {
                return@filter false
            }

            // 2. Filter by Status Category
            if (filter.statuses.isNotEmpty() && req.statusCategory !in filter.statuses) {
                return@filter false
            }

            // 3. Filter by HTTPS only
            if (filter.httpsOnly && !req.isHttps) {
                return@filter false
            }

            // 4. Filter by Errors only
            if (filter.errorsOnly && !req.isError) {
                return@filter false
            }

            // 5. Search query matching
            if (trimmedQuery.isNotEmpty()) {
                val matchesUrl = req.url.contains(trimmedQuery, ignoreCase = true)
                val matchesHost = req.host.contains(trimmedQuery, ignoreCase = true)
                val matchesPath = req.path.contains(trimmedQuery, ignoreCase = true)
                val matchesMethod = req.method.name.equals(trimmedQuery, ignoreCase = true)
                val matchesStatus = req.statusCode.toString() == trimmedQuery ||
                        req.statusMessage.contains(trimmedQuery, ignoreCase = true)

                if (!matchesUrl && !matchesHost && !matchesPath && !matchesMethod && !matchesStatus) {
                    return@filter false
                }
            }

            true
        }
    }
}

class ApiViewModelFactory(
    private val repository: ApiRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiViewModel::class.java)) {
            return ApiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
