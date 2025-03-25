package com.tien.piholeconnect.ui

import androidx.lifecycle.ViewModel
import com.tien.piholeconnect.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel
@Inject
constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    val selectedPiHoleFlow = userPreferencesRepository.selectedPiHoleFlow
    val updateUserPreferences = userPreferencesRepository::updateUserPreferences
}
