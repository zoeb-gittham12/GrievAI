package com.rork.grievai.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * App-wide session state. Backed by real Supabase Auth + the `profiles` table.
 */
class SessionViewModel : ViewModel() {

    private val supa = SupabaseClient.client

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val isLoggedIn: Boolean get() = _user.value != null
    val isStudent: Boolean get() = _user.value?.role == UserRole.STUDENT
    val isAdmin: Boolean get() = _user.value?.role == UserRole.ADMIN

    init {
        // Restore session automatically if the user was already logged in
        // (Supabase persists the session token on-device between app launches).
        viewModelScope.launch {
            val status = supa.auth.sessionStatus.value
            if (status is SessionStatus.Authenticated) {
                fetchProfileIntoState(status.session.user?.id)
            }
        }
    }

    fun login(email: String, password: String, role: UserRole, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                supa.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val userId = supa.auth.currentUserOrNull()?.id
                val profile = fetchProfileWithInstitution(userId)
                if (profile == null) {
                    _error.value = "Could not load your profile. Please try again."
                } else if (profile.role != role) {
                    supa.auth.signOut()
                    _error.value = "This account is registered as ${profile.role.name.lowercase()}, not ${role.name.lowercase()}."
                } else {
                    _user.value = profile
                    onSuccess()
                }
            } catch (e: RestException) {
                _error.value = "Invalid email or password."
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed. Check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * @param joinCode Required for students, and for admins joining an existing
     *   university. Ignored if [newUniversityName] is provided.
     * @param newUniversityName If set, this signup creates a brand-new university
     *   (intended for the first admin of a college) and [joinCode] is ignored.
     */
    fun signup(
        name: String,
        email: String,
        idCardNumber: String,
        password: String,
        role: UserRole,
        department: String,
        enrollmentNumber: String,
        joinCode: String = "",
        newUniversityName: String = "",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            if (name.isBlank() || email.isBlank() || password.length < 6) {
                _error.value = "Please fill all fields. Password must be 6+ characters."
                _isLoading.value = false
                return@launch
            }
            if (newUniversityName.isBlank() && joinCode.isBlank()) {
                _error.value = "Enter your university's join code, or create a new university."
                _isLoading.value = false
                return@launch
            }
            try {
                supa.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject {
                        put("name", name)
                        put("id_card_number", idCardNumber)
                        put("role", role.name)
                        put("department", department)
                        put("enrollment_number", enrollmentNumber)
                        if (newUniversityName.isNotBlank()) {
                            put("new_university_name", newUniversityName)
                        } else {
                            put("join_code", joinCode.uppercase())
                        }
                    }
                }
                val userId = supa.auth.currentUserOrNull()?.id
                val profile = fetchProfileWithInstitution(userId)
                if (profile == null) {
                    _error.value = "Account created, but profile setup failed. Try logging in."
                } else {
                    _user.value = profile
                    onSuccess()
                }
            } catch (e: RestException) {
                _error.value = when {
                    e.message?.contains("Invalid join code", ignoreCase = true) == true ->
                        "That join code doesn't match any university. Double-check with your admin."
                    e.message?.contains("already registered", ignoreCase = true) == true ||
                            e.message?.contains("already exists", ignoreCase = true) == true ->
                        "That email is already registered."
                    else -> "Signup failed: ${e.message}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Signup failed. Check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            supa.auth.signOut()
            _user.value = null
        }
    }

    fun clearError() { _error.value = null }

    fun updateProfile(name: String, department: String, enrollmentNumber: String) {
        val current = _user.value ?: return
        viewModelScope.launch {
            try {
                supa.postgrest["profiles"].update({
                    set("name", name)
                    set("department", department)
                    set("enrollment_number", enrollmentNumber)
                }) {
                    filter { eq("id", current.id) }
                }
                _user.value = current.copy(
                    name = name,
                    department = department,
                    enrollmentNumber = enrollmentNumber
                )
            } catch (e: Exception) {
                _error.value = "Could not update profile: ${e.message}"
            }
        }
    }

    private suspend fun fetchProfileWithInstitution(userId: String?): User? {
        if (userId == null) return null
        return try {
            val profile = supa.postgrest["profiles"]
                .select(columns = Columns.ALL) { filter { eq("id", userId) } }
                .decodeSingle<ProfileDto>()
            val (institutionName, institutionJoinCode) = try {
                val uni = supa.postgrest["universities"]
                    .select(columns = Columns.ALL) { filter { eq("id", profile.institutionId) } }
                    .decodeSingle<UniversityDto>()
                uni.name to uni.joinCode
            } catch (e: Exception) {
                "" to ""
            }
            profile.toUser(institutionName, institutionJoinCode)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchProfileIntoState(userId: String?) {
        _user.value = fetchProfileWithInstitution(userId)
    }
}