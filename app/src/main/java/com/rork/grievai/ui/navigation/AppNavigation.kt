package com.rork.grievai.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rork.grievai.data.AppPreferences
import com.rork.grievai.data.SessionViewModel
import com.rork.grievai.data.UserRole
import com.rork.grievai.ui.screens.auth.ForgotPasswordScreen
import com.rork.grievai.ui.screens.auth.LoginScreen
import com.rork.grievai.ui.screens.auth.OnboardingScreen
import com.rork.grievai.ui.screens.auth.OtpVerificationScreen
import com.rork.grievai.ui.screens.auth.PinSetupScreen
import com.rork.grievai.ui.screens.auth.PinUnlockScreen
import com.rork.grievai.ui.screens.auth.RoleSelectionScreen
import com.rork.grievai.ui.screens.auth.SignUpScreen
import com.rork.grievai.ui.screens.auth.SplashScreen
import com.rork.grievai.ui.screens.student.ComplaintDetailScreen
import com.rork.grievai.ui.screens.student.SubmitComplaintScreen
import kotlinx.coroutines.delay

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val OTP = "otp"
    const val PIN_SETUP = "pin_setup"
    const val PIN_UNLOCK = "pin_unlock"
    const val MAIN = "main"
    const val COMPLAINT_DETAIL = "complaint_detail"
    const val SUBMIT_COMPLAINT = "submit_complaint"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = viewModel()
    val user by sessionViewModel.user.collectAsState()
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }

    fun goMain() {
        navController.navigate(Routes.MAIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun goAfterAuth() {
        if (!prefs.hasPin()) {
            navController.navigate(Routes.PIN_SETUP) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            goMain()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 6 } },
        exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 6 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 6 } },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 6 } }
    ) {
        composable(Routes.SPLASH) {
            val sessionReady by sessionViewModel.sessionReady.collectAsState()
            SplashScreen(onNavigateNext = { /* driven below */ })
            LaunchedEffect(sessionReady) {
                if (!sessionReady) return@LaunchedEffect
                delay(400)
                val next = when {
                    !prefs.hasCompletedOnboarding -> Routes.ONBOARDING
                    prefs.hasPin() -> Routes.PIN_UNLOCK   // PIN exists → always unlock
                    user != null -> Routes.PIN_SETUP
                    else -> Routes.ROLE_SELECTION
                }
                navController.navigate(next) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinish = {
                prefs.hasCompletedOnboarding = true
                navController.navigate(Routes.ROLE_SELECTION) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(onRoleSelected = { role ->
                prefs.lastRole = role.name
                navController.navigate("${Routes.LOGIN}/${role.name}")
            })
        }

        composable("${Routes.LOGIN}/{role}") { backStackEntry ->
            val role = UserRole.valueOf(
                backStackEntry.arguments?.getString("role") ?: UserRole.STUDENT.name
            )
            LoginScreen(
                role = role,
                onBack = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    prefs.lastRole = role.name
                    goAfterAuth()
                },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onSignUp = { navController.navigate("${Routes.SIGN_UP}/${role.name}") },
                sessionViewModel = sessionViewModel
            )
        }

        composable("${Routes.SIGN_UP}/{role}") { backStackEntry ->
            val role = UserRole.valueOf(
                backStackEntry.arguments?.getString("role") ?: UserRole.STUDENT.name
            )
            SignUpScreen(
                role = role,
                onBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    prefs.lastRole = role.name
                    goAfterAuth()
                },
                onLogin = { navController.popBackStack() },
                sessionViewModel = sessionViewModel
            )
        }

        composable(Routes.PIN_SETUP) {
            PinSetupScreen(onPinSet = { pin ->
                prefs.setPin(pin)
                goMain()
            })
        }

        composable(Routes.PIN_UNLOCK) {
            PinUnlockScreen(
                verifyPin = { prefs.verifyPin(it) },
                onUnlocked = { goMain() },
                onUsePassword = {
                    sessionViewModel.logout()
                    prefs.clearPin()
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSendOtp = { navController.navigate(Routes.OTP) }
            )
        }

        composable(Routes.OTP) {
            OtpVerificationScreen(
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            val currentUser = user
            if (currentUser != null) {
                MainApp(
                    user = currentUser,
                    onComplaintClick = { complaintId ->
                        navController.navigate("${Routes.COMPLAINT_DETAIL}/$complaintId")
                    },
                    onSubmitComplaint = {
                        navController.navigate(Routes.SUBMIT_COMPLAINT)
                    },
                    onLogout = {
                        sessionViewModel.logout()
                        prefs.clearPin()
                        navController.navigate(Routes.ROLE_SELECTION) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProfile = { name, dept, enroll ->
                        sessionViewModel.updateProfile(name, dept, enroll)
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        composable("${Routes.COMPLAINT_DETAIL}/{complaintId}") { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
            val currentUser = user
            if (currentUser != null) {
                ComplaintDetailScreen(
                    complaintId = complaintId,
                    user = currentUser,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.SUBMIT_COMPLAINT) {
            val currentUser = user
            if (currentUser != null) {
                SubmitComplaintScreen(
                    user = currentUser,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { complaintId ->
                        navController.navigate("${Routes.COMPLAINT_DETAIL}/$complaintId") {
                            popUpTo(Routes.SUBMIT_COMPLAINT) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}