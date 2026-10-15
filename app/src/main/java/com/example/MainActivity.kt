package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Goal
import com.example.ui.components.CelebrationDialog
import com.example.ui.screens.AddEditGoalScreen
import com.example.ui.screens.AddMilestoneScreen
import com.example.ui.screens.CompletedGoalsScreen
import com.example.ui.screens.GoalDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DeepPurple
import com.example.ui.theme.MilestoneTheme
import com.example.ui.theme.PurplePastel
import com.example.ui.viewmodel.MilestoneViewModel

sealed class Screen {
    object Home : Screen()
    object Completed : Screen()
    object Settings : Screen()
    data class GoalDetail(val goalId: Long) : Screen()
    data class AddEditGoal(val goalId: Long? = null) : Screen()
    data class AddMilestone(val goalId: Long? = null) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val milestoneViewModel: MilestoneViewModel = viewModel()
            val themeMode by milestoneViewModel.themeMode.collectAsStateWithLifecycle()

            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MilestoneTheme(darkTheme = isDarkTheme) {
                MilestoneApp(viewModel = milestoneViewModel)
            }
        }
    }
}

@Composable
fun MilestoneApp(viewModel: MilestoneViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val activeGoals by viewModel.filteredActiveGoals.collectAsStateWithLifecycle()
    val allActiveGoals by viewModel.allActiveGoals.collectAsStateWithLifecycle()
    val completedGoals by viewModel.completedGoals.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val celebrationGoal by viewModel.celebrationGoal.collectAsStateWithLifecycle()
    val reminderFreq by viewModel.reminderFrequency.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val defaultProgressStyle by viewModel.defaultProgressStyle.collectAsStateWithLifecycle()

    val isTopLevelScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.Completed ||
            currentScreen is Screen.Settings

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        onClick = { currentScreen = Screen.Home },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.Home) Icons.Filled.Flag else Icons.Outlined.Flag,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Goals") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepPurple,
                            selectedTextColor = DeepPurple,
                            indicatorColor = PurplePastel
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Completed,
                        onClick = { currentScreen = Screen.Completed },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.Completed) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = "Completed"
                            )
                        },
                        label = { Text("Conquered") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD97706),
                            selectedTextColor = Color(0xFFD97706),
                            indicatorColor = Color(0xFFFEF3C7)
                        ),
                        modifier = Modifier.testTag("nav_completed")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Settings,
                        onClick = { currentScreen = Screen.Settings },
                        icon = {
                            Icon(
                                if (currentScreen is Screen.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepPurple,
                            selectedTextColor = DeepPurple,
                            indicatorColor = PurplePastel
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 4 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut())
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Home -> {
                        HomeScreen(
                            activeGoals = activeGoals,
                            completedGoalsCount = completedGoals.size,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.setCategoryFilter(it) },
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onGoalClick = { goalId ->
                                viewModel.selectGoal(goalId)
                                currentScreen = Screen.GoalDetail(goalId)
                            },
                            onAddNewGoal = {
                                viewModel.setEditingGoal(null)
                                currentScreen = Screen.AddEditGoal(null)
                            }
                        )
                    }

                    is Screen.Completed -> {
                        CompletedGoalsScreen(
                            completedGoals = completedGoals,
                            onGoalClick = { goalId ->
                                viewModel.selectGoal(goalId)
                                currentScreen = Screen.GoalDetail(goalId)
                            },
                            onReopenGoal = { goalId ->
                                viewModel.toggleGoalCompletion(goalId, currentCompleted = true)
                            }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            currentReminderFrequency = reminderFreq,
                            onReminderFrequencyChanged = { viewModel.setReminderFrequency(it) },
                            currentThemeMode = themeMode,
                            onThemeModeChanged = { viewModel.setThemeMode(it) },
                            defaultProgressStyle = defaultProgressStyle,
                            onDefaultProgressStyleChanged = { viewModel.setDefaultProgressStyle(it) },
                            onResetToSampleData = { viewModel.resetToSampleData() }
                        )
                    }

                    is Screen.GoalDetail -> {
                        val selectedGoalWithMilestones by viewModel.selectedGoalWithMilestones.collectAsStateWithLifecycle()

                        GoalDetailScreen(
                            goalWithMilestones = selectedGoalWithMilestones,
                            onBack = { currentScreen = Screen.Home },
                            onEditGoal = {
                                viewModel.setEditingGoal(selectedGoalWithMilestones?.goal)
                                currentScreen = Screen.AddEditGoal(screen.goalId)
                            },
                            onAddMilestoneClick = {
                                currentScreen = Screen.AddMilestone(screen.goalId)
                            },
                            onToggleMilestone = { milestoneId, isCompleted ->
                                viewModel.toggleMilestoneCompletion(milestoneId, isCompleted)
                            },
                            onDeleteMilestone = { milestoneId ->
                                viewModel.deleteMilestone(milestoneId)
                            },
                            onToggleGoalCompletion = { isCompleted ->
                                viewModel.toggleGoalCompletion(screen.goalId, currentCompleted = !isCompleted)
                            },
                            onSaveNotes = { notes ->
                                viewModel.updateGoalNotes(screen.goalId, notes)
                            }
                        )
                    }

                    is Screen.AddEditGoal -> {
                        val editingGoal by viewModel.editingGoal.collectAsStateWithLifecycle()

                        AddEditGoalScreen(
                            goalToEdit = editingGoal,
                            onSaveGoal = { title, description, category, targetDateEpochMs, emoji, displayStyle, colorHex, id ->
                                viewModel.saveGoal(
                                    title = title,
                                    description = description,
                                    category = category,
                                    targetDateEpochMs = targetDateEpochMs,
                                    emoji = emoji,
                                    displayStyle = displayStyle,
                                    colorHex = colorHex,
                                    existingId = id
                                ) { savedId ->
                                    viewModel.selectGoal(savedId)
                                    currentScreen = Screen.GoalDetail(savedId)
                                }
                            },
                            onDeleteGoal = if (screen.goalId != null) {
                                { id ->
                                    viewModel.deleteGoal(id) {
                                        currentScreen = Screen.Home
                                    }
                                }
                            } else null,
                            onBack = {
                                if (screen.goalId != null) {
                                    currentScreen = Screen.GoalDetail(screen.goalId)
                                } else {
                                    currentScreen = Screen.Home
                                }
                            }
                        )
                    }

                    is Screen.AddMilestone -> {
                        val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()

                        AddMilestoneScreen(
                            goals = allGoals,
                            initialGoalId = screen.goalId,
                            onSaveMilestone = { goalId, title, dueDate ->
                                viewModel.saveMilestone(
                                    goalId = goalId,
                                    title = title,
                                    dueDateEpochMs = dueDate
                                ) {
                                    viewModel.selectGoal(goalId)
                                    currentScreen = Screen.GoalDetail(goalId)
                                }
                            },
                            onBack = {
                                if (screen.goalId != null) {
                                    currentScreen = Screen.GoalDetail(screen.goalId)
                                } else {
                                    currentScreen = Screen.Home
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Celebration modal when a goal is completed
    celebrationGoal?.let { goalWithMilestones ->
        CelebrationDialog(
            goalWithMilestones = goalWithMilestones,
            onDismiss = { viewModel.clearCelebration() },
            onViewInArchive = {
                viewModel.clearCelebration()
                currentScreen = Screen.Completed
            }
        )
    }
}
