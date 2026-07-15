package com.orbits.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.app.domain.model.Orbit
import com.orbits.app.domain.repository.OrbitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Unified UI State for the Home screen to enforce Unidirectional Data Flow.
 */
data class HomeUiState(
    val orbits: List<Orbit> = emptyList(),
    val activeTags: List<String> = emptyList(),
    val reminderOrbits: List<Orbit> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Orbits application screens.
 * Uses OrbitRepository to enforce separation of concerns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrbitViewModel(private val repository: OrbitRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _timeFilter = MutableStateFlow<String?>(null)
    val timeFilter: StateFlow<String?> = _timeFilter.asStateFlow()

    private val _activeTags = repository.getAllOrbits().map { orbits ->
        val tags = orbits.flatMap { it.tags }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        tags + "Archived"
    }

    private val _filteredOrbits = combine(
        repository.getAllOrbits(),
        _searchQuery,
        _selectedTag,
        _timeFilter
    ) { orbits, query, tag, time ->
        val now = System.currentTimeMillis()
        orbits.filter { orbit ->
            val isArchived = orbit.status == "Archived"
            val matchesArchiveFilter = if (tag == "Archived") isArchived else !isArchived

            if (!matchesArchiveFilter) return@filter false

            val matchesQuery = query.isBlank() || 
                orbit.name.contains(query, ignoreCase = true) ||
                (orbit.headline?.contains(query, ignoreCase = true) ?: false) ||
                orbit.tags.any { it.contains(query, ignoreCase = true) } ||
                (orbit.notes?.contains(query, ignoreCase = true) ?: false)

            val matchesTag = tag == "Archived" || tag == null || orbit.tags.contains(tag)

            val matchesTime = when (time) {
                "Added This Week" -> now - orbit.createdAt <= java.util.concurrent.TimeUnit.DAYS.toMillis(7)
                "Added This Month" -> now - orbit.createdAt <= java.util.concurrent.TimeUnit.DAYS.toMillis(30)
                "Older than 6 Months" -> now - orbit.createdAt > java.util.concurrent.TimeUnit.DAYS.toMillis(180)
                else -> true
            }

            matchesQuery && matchesTag && matchesTime
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _filteredOrbits,
        _activeTags,
        repository.getOrbitsWithReminders()
    ) { orbits, tags, reminders ->
        HomeUiState(
            orbits = orbits,
            activeTags = tags,
            reminderOrbits = reminders,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun selectTimeFilter(filter: String?) {
        _timeFilter.value = filter
    }

    fun archiveOrbit(orbit: Orbit) {
        viewModelScope.launch {
            repository.archiveOrbit(orbit)
        }
    }

    fun deleteOrbit(orbit: Orbit) {
        viewModelScope.launch {
            repository.deleteOrbit(orbit)
        }
    }

    fun insertOrbit(orbit: Orbit) {
        viewModelScope.launch {
            repository.insertOrbit(orbit)
        }
    }

    fun restoreOrbit(orbit: Orbit) {
        viewModelScope.launch {
            repository.restoreOrbit(orbit)
        }
    }


}

class OrbitViewModelFactory(private val repository: OrbitRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrbitViewModel::class.java)) {
            return OrbitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
