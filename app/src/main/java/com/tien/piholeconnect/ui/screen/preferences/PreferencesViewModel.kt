package com.tien.piholeconnect.ui.screen.preferences

import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel
@Inject
constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    val userPreferencesFlow = userPreferencesRepository.userPreferences
    val updateUserPreferences = userPreferencesRepository::updateUserPreferences
}
