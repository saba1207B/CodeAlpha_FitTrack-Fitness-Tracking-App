package com.example.fittrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.database.FitTrackDatabase
import com.example.fittrack.data.entity.DailyStep
import com.example.fittrack.data.entity.Workout
import com.example.fittrack.data.preferences.UserPreferencesRepository
import com.example.fittrack.data.repository.FitTrackRepository
import com.example.fittrack.model.DayStat
import com.example.fittrack.model.ExerciseType
import com.example.fittrack.model.WeeklySummary
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DashboardUiState(
    val todaySteps: Int = 0,
    val stepGoal: Int = 10000,
    val todayCalories: Int = 0,
    val todayDurationMinutes: Int = 0,
    val todayWorkoutCount: Int = 0,
    val todayWorkouts: List<Workout> = emptyList(),
    val currentDateFormatted: String = "",
    val isStepDialogOpen: Boolean = false
)

data class AddEditWorkoutUiState(
    val isEditing: Boolean = false,
    val editWorkoutId: Long = 0L,
    val exerciseType: String = ExerciseType.RUNNING.displayName,
    val durationMinutesText: String = "",
    val caloriesBurnedText: String = "",
    val selectedDateEpochMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val durationError: String? = null,
    val caloriesError: String? = null,
    val isAutoCalorieSuggested: Boolean = false
)

data class WorkoutHistoryUiState(
    val allWorkouts: List<Workout> = emptyList(),
    val filteredWorkouts: List<Workout> = emptyList(),
    val selectedFilter: String = "All",
    val deleteCandidate: Workout? = null
)

data class StatisticsUiState(
    val weeklySummary: WeeklySummary = WeeklySummary(),
    val selectedChartTab: Int = 0 // 0: Calories, 1: Duration, 2: Steps
)

data class SettingsUiState(
    val stepGoal: Int = 10000,
    val isGoalDialogOpen: Boolean = false,
    val isResetDialogOpen: Boolean = false,
    val appVersion: String = "1.0.0"
)

class FitTrackViewModel(
    application: Application,
    private val repository: FitTrackRepository
) : AndroidViewModel(application) {

    private val zoneId = ZoneId.systemDefault()
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    // Snackbars / Events
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dashboard State
    private val _isStepDialogOpen = MutableStateFlow(false)
    val isStepDialogOpen: StateFlow<Boolean> = _isStepDialogOpen.asStateFlow()

    // Add / Edit State
    private val _addEditState = MutableStateFlow(
        AddEditWorkoutUiState(
            selectedDateEpochMillis = System.currentTimeMillis()
        )
    )
    val addEditState: StateFlow<AddEditWorkoutUiState> = _addEditState.asStateFlow()

    // History filter & delete
    private val _historyFilter = MutableStateFlow("All")
    private val _deleteCandidate = MutableStateFlow<Workout?>(null)
    val deleteCandidate: StateFlow<Workout?> = _deleteCandidate.asStateFlow()

    // Statistics tab
    private val _selectedStatsTab = MutableStateFlow(0)
    val selectedStatsTab: StateFlow<Int> = _selectedStatsTab.asStateFlow()

    // Settings dialogs
    private val _isGoalDialogOpen = MutableStateFlow(false)
    val isGoalDialogOpen: StateFlow<Boolean> = _isGoalDialogOpen.asStateFlow()
    private val _isResetDialogOpen = MutableStateFlow(false)
    val isResetDialogOpen: StateFlow<Boolean> = _isResetDialogOpen.asStateFlow()

    // Today's steps flow
    private val todayLocalDate: LocalDate get() = LocalDate.now(zoneId)
    private val todayEpochDay: Long get() = todayLocalDate.toEpochDay()

    val todayStepsFlow = repository.getStepsForDate(todayEpochDay)
    val stepGoalFlow = repository.stepGoal

    // Combine for Dashboard UI State
    val dashboardUiState: StateFlow<DashboardUiState> = combine(
        repository.allWorkouts,
        todayStepsFlow,
        stepGoalFlow,
        _isStepDialogOpen
    ) { workouts, todayStepEntity, stepGoal, isDialogOpen ->
        val today = LocalDate.now(zoneId)
        val todayStartMillis = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val todayEndMillis = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val todayWorkouts = workouts.filter { it.date in todayStartMillis..todayEndMillis }
        val todayCalories = todayWorkouts.sumOf { it.caloriesBurned }
        val todayDuration = todayWorkouts.sumOf { it.durationMinutes }

        DashboardUiState(
            todaySteps = todayStepEntity?.steps ?: 0,
            stepGoal = stepGoal,
            todayCalories = todayCalories,
            todayDurationMinutes = todayDuration,
            todayWorkoutCount = todayWorkouts.size,
            todayWorkouts = todayWorkouts,
            currentDateFormatted = today.format(dateFormatter),
            isStepDialogOpen = isDialogOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(
            currentDateFormatted = LocalDate.now(zoneId).format(dateFormatter)
        )
    )

    // Combine for History UI State
    val historyUiState: StateFlow<WorkoutHistoryUiState> = combine(
        repository.allWorkouts,
        _historyFilter,
        _deleteCandidate
    ) { workouts, filter, candidate ->
        val filtered = if (filter == "All") {
            workouts
        } else {
            workouts.filter { it.exerciseType.equals(filter, ignoreCase = true) }
        }
        WorkoutHistoryUiState(
            allWorkouts = workouts,
            filteredWorkouts = filtered,
            selectedFilter = filter,
            deleteCandidate = candidate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutHistoryUiState()
    )

    // Combine for Statistics UI State (last 7 calendar days)
    val statisticsUiState: StateFlow<StatisticsUiState> = combine(
        repository.allWorkouts,
        repository.getStepsBetweenDays(
            LocalDate.now(zoneId).minusDays(6).toEpochDay(),
            LocalDate.now(zoneId).toEpochDay()
        ),
        _selectedStatsTab
    ) { workouts, stepsList, tabIndex ->
        val today = LocalDate.now(zoneId)
        val stepsByDay = stepsList.associateBy { it.dateEpochDay }

        val dayStats = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val startMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endMillis = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

            val dayWorkouts = workouts.filter { it.date in startMillis..endMillis }
            val dayCalories = dayWorkouts.sumOf { it.caloriesBurned }
            val dayDuration = dayWorkouts.sumOf { it.durationMinutes }
            val daySteps = stepsByDay[date.toEpochDay()]?.steps ?: 0

            DayStat(
                date = date,
                dayLabel = date.format(dayOfWeekFormatter),
                formattedDate = date.format(shortDateFormatter),
                steps = daySteps,
                calories = dayCalories,
                durationMinutes = dayDuration,
                workoutCount = dayWorkouts.size
            )
        }

        val totalSteps = dayStats.sumOf { it.steps }
        val totalCalories = dayStats.sumOf { it.calories }
        val totalDuration = dayStats.sumOf { it.durationMinutes }
        val totalWorkouts = dayStats.sumOf { it.workoutCount }
        val avgSteps = if (dayStats.isNotEmpty()) totalSteps / dayStats.size else 0

        val weeklySummary = WeeklySummary(
            totalSteps = totalSteps,
            totalCalories = totalCalories,
            totalDurationMinutes = totalDuration,
            totalWorkouts = totalWorkouts,
            averageDailySteps = avgSteps,
            days = dayStats
        )

        StatisticsUiState(
            weeklySummary = weeklySummary,
            selectedChartTab = tabIndex
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    // Settings UI State
    val settingsUiState: StateFlow<SettingsUiState> = combine(
        stepGoalFlow,
        _isGoalDialogOpen,
        _isResetDialogOpen
    ) { goal, isGoalOpen, isResetOpen ->
        SettingsUiState(
            stepGoal = goal,
            isGoalDialogOpen = isGoalOpen,
            isResetDialogOpen = isResetOpen,
            appVersion = "1.0.0"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    // -------------------------------------------------------------
    // Actions: Steps
    // -------------------------------------------------------------
    fun setStepDialogOpen(isOpen: Boolean) {
        _isStepDialogOpen.value = isOpen
    }

    fun updateTodaySteps(steps: Int) {
        viewModelScope.launch {
            repository.updateSteps(todayEpochDay, steps.coerceAtLeast(0))
            _isStepDialogOpen.value = false
            _userMessage.emit("Steps updated successfully!")
        }
    }

    // -------------------------------------------------------------
    // Actions: Add / Edit Workout Form
    // -------------------------------------------------------------
    fun onExerciseTypeChanged(type: String) {
        _addEditState.update { current ->
            val durationInt = current.durationMinutesText.toIntOrNull() ?: 0
            val newCaloriesText = if (current.caloriesBurnedText.isBlank() || current.isAutoCalorieSuggested) {
                if (durationInt > 0) {
                    val rate = ExerciseType.fromString(type).defaultCaloriesPerMinute
                    (durationInt * rate).toInt().toString()
                } else ""
            } else current.caloriesBurnedText

            current.copy(
                exerciseType = type,
                caloriesBurnedText = newCaloriesText,
                isAutoCalorieSuggested = newCaloriesText.isNotBlank()
            )
        }
    }

    fun onDurationChanged(text: String) {
        val filtered = text.filter { it.isDigit() }
        _addEditState.update { current ->
            val durationInt = filtered.toIntOrNull() ?: 0
            val autoCalories = if (filtered.isNotBlank() && (current.caloriesBurnedText.isBlank() || current.isAutoCalorieSuggested)) {
                val rate = ExerciseType.fromString(current.exerciseType).defaultCaloriesPerMinute
                (durationInt * rate).toInt().toString()
            } else current.caloriesBurnedText

            current.copy(
                durationMinutesText = filtered,
                caloriesBurnedText = autoCalories,
                isAutoCalorieSuggested = autoCalories.isNotBlank() && current.caloriesBurnedText == autoCalories,
                durationError = null
            )
        }
    }

    fun onCaloriesChanged(text: String) {
        val filtered = text.filter { it.isDigit() }
        _addEditState.update {
            it.copy(
                caloriesBurnedText = filtered,
                isAutoCalorieSuggested = false,
                caloriesError = null
            )
        }
    }

    fun onDateChanged(epochMillis: Long) {
        _addEditState.update { it.copy(selectedDateEpochMillis = epochMillis) }
    }

    fun onNotesChanged(text: String) {
        _addEditState.update { it.copy(notes = text) }
    }

    fun clearWorkoutForm() {
        _addEditState.value = AddEditWorkoutUiState(
            isEditing = false,
            editWorkoutId = 0L,
            exerciseType = ExerciseType.RUNNING.displayName,
            durationMinutesText = "",
            caloriesBurnedText = "",
            selectedDateEpochMillis = System.currentTimeMillis(),
            notes = "",
            durationError = null,
            caloriesError = null
        )
    }

    fun loadWorkoutForEdit(workout: Workout) {
        _addEditState.value = AddEditWorkoutUiState(
            isEditing = true,
            editWorkoutId = workout.id,
            exerciseType = workout.exerciseType,
            durationMinutesText = workout.durationMinutes.toString(),
            caloriesBurnedText = workout.caloriesBurned.toString(),
            selectedDateEpochMillis = workout.date,
            notes = workout.notes,
            durationError = null,
            caloriesError = null
        )
    }

    fun saveWorkout(onSuccess: () -> Unit) {
        val state = _addEditState.value
        var hasError = false
        var durationErr: String? = null
        var caloriesErr: String? = null

        val durationInt = state.durationMinutesText.toIntOrNull()
        if (durationInt == null || durationInt <= 0) {
            durationErr = "Duration must be greater than 0 minutes"
            hasError = true
        }

        val caloriesInt = state.caloriesBurnedText.toIntOrNull()
        if (caloriesInt == null || caloriesInt < 0) {
            caloriesErr = "Calories cannot be negative"
            hasError = true
        }

        if (state.exerciseType.isBlank()) {
            hasError = true
        }

        if (hasError) {
            _addEditState.update {
                it.copy(
                    durationError = durationErr,
                    caloriesError = caloriesErr
                )
            }
            return
        }

        viewModelScope.launch {
            val workout = Workout(
                id = if (state.isEditing) state.editWorkoutId else 0L,
                exerciseType = state.exerciseType,
                durationMinutes = durationInt ?: 0,
                caloriesBurned = caloriesInt ?: 0,
                date = state.selectedDateEpochMillis,
                notes = state.notes.trim()
            )

            if (state.isEditing) {
                repository.updateWorkout(workout)
                _userMessage.emit("Workout updated successfully!")
            } else {
                repository.insertWorkout(workout)
                _userMessage.emit("Workout saved successfully!")
            }

            clearWorkoutForm()
            onSuccess()
        }
    }

    // -------------------------------------------------------------
    // Actions: History & Delete
    // -------------------------------------------------------------
    fun setHistoryFilter(filter: String) {
        _historyFilter.value = filter
    }

    fun requestDeleteWorkout(workout: Workout) {
        _deleteCandidate.value = workout
    }

    fun dismissDeleteDialog() {
        _deleteCandidate.value = null
    }

    fun confirmDeleteWorkout() {
        val workout = _deleteCandidate.value ?: return
        viewModelScope.launch {
            repository.deleteWorkout(workout)
            _deleteCandidate.value = null
            _userMessage.emit("Workout deleted.")
        }
    }

    // -------------------------------------------------------------
    // Actions: Statistics
    // -------------------------------------------------------------
    fun setSelectedStatsTab(tabIndex: Int) {
        _selectedStatsTab.value = tabIndex
    }

    // -------------------------------------------------------------
    // Actions: Settings
    // -------------------------------------------------------------
    fun setGoalDialogOpen(isOpen: Boolean) {
        _isGoalDialogOpen.value = isOpen
    }

    fun setResetDialogOpen(isOpen: Boolean) {
        _isResetDialogOpen.value = isOpen
    }

    fun updateStepGoal(newGoal: Int) {
        viewModelScope.launch {
            repository.setStepGoal(newGoal.coerceAtLeast(100))
            _isGoalDialogOpen.value = false
            _userMessage.emit("Daily step goal updated!")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _isResetDialogOpen.value = false
            clearWorkoutForm()
            _userMessage.emit("All data has been reset.")
        }
    }

    // -------------------------------------------------------------
    // Sample Data Seeding (For manual demo or quick exploration)
    // -------------------------------------------------------------
    fun seedSampleData() {
        viewModelScope.launch {
            val today = LocalDate.now(zoneId)
            val sampleWorkouts = mutableListOf<Workout>()
            val sampleSteps = mutableListOf<DailyStep>()

            val exerciseTypes = listOf(
                ExerciseType.RUNNING.displayName,
                ExerciseType.CYCLING.displayName,
                ExerciseType.WALKING.displayName,
                ExerciseType.SWIMMING.displayName,
                ExerciseType.STRENGTH_TRAINING.displayName,
                ExerciseType.YOGA.displayName,
                ExerciseType.CARDIO.displayName
            )

            val stepCounts = listOf(8420, 10250, 7150, 12400, 6800, 9300, 11500)
            val durations = listOf(35, 45, 30, 50, 40, 25, 60)
            val calories = listOf(320, 410, 150, 480, 260, 120, 510)

            for (i in 0..6) {
                val date = today.minusDays(i.toLong())
                val epochDay = date.toEpochDay()
                val timestamp = date.atTime(10 + (i % 8), 30).atZone(zoneId).toInstant().toEpochMilli()

                sampleSteps.add(
                    DailyStep(
                        dateEpochDay = epochDay,
                        steps = stepCounts[i % stepCounts.size]
                    )
                )

                sampleWorkouts.add(
                    Workout(
                        exerciseType = exerciseTypes[i % exerciseTypes.size],
                        durationMinutes = durations[i % durations.size],
                        caloriesBurned = calories[i % calories.size],
                        date = timestamp,
                        notes = "Great workout session with high intensity."
                    )
                )
            }

            repository.insertSampleWorkouts(sampleWorkouts, sampleSteps)
            _userMessage.emit("Sample fitness data added!")
        }
    }
}

class FitTrackViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitTrackViewModel::class.java)) {
            val database = FitTrackDatabase.getDatabase(application)
            val preferences = UserPreferencesRepository(application)
            val repository = FitTrackRepository(
                workoutDao = database.workoutDao(),
                dailyStepDao = database.dailyStepDao(),
                preferencesRepository = preferences
            )
            return FitTrackViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
