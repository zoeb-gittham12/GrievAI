package com.rork.grievai.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rork.grievai.data.SessionViewModel
import com.rork.grievai.data.UserRole
import com.rork.grievai.ui.screens.auth.ForgotPasswordScreen
import com.rork.grievai.ui.screens.auth.LoginScreen
import com.rork.grievai.ui.screens.auth.OnboardingScreen
import com.rork.grievai.ui.screens.auth.OtpVerificationScreen
import com.rork.grievai.ui.screens.auth.RoleSelectionScreen
import com.rork.grievai.ui.screens.auth.SignUpScreen
import com.rork.grievai.ui.screens.auth.SplashScreen
import com.rork.grievai.ui.screens.student.ComplaintDetailScreen
import com.rork.grievai.ui.screens.student.SubmitComplaintScreen

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val OTP = "otp"
    const val MAIN = "main"
    const val COMPLAINT_DETAIL = "complaint_detail"
    const val SUBMIT_COMPLAINT = "submit_complaint"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = viewModel()
    val user by sessionViewModel.user.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 6 } },
        exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 6 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 6 } },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 6 } }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onNavigateNext = {
                navController.navigate(Routes.ONBOARDING) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinish = {
                navController.navigate(Routes.ROLE_SELECTION) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(onRoleSelected = { role ->
                navController.navigate("${Routes.LOGIN}/${role.name}") {
                    popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                }
            })
        }

        composable("${Routes.LOGIN}/{role}") { backStackEntry ->
            val role = UserRole.valueOf(
                backStackEntry.arguments?.getString("role") ?: UserRole.STUDENT.name
            )
            LoginScreen(
                role = role,
                onBack = { navController.navigate(Routes.ROLE_SELECTION) { popUpTo(0) } },
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
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
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLogin = { navController.popBackStack() },
                sessionViewModel = sessionViewModel
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
                        navController.navigate(Routes.ROLE_SELECTION) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProfile = { name, dept, enroll ->
                        sessionViewModel.updateProfile(name, dept, enroll)
                    }
                )
            }
        }

        composable("${Routes.COMPLAINT_DETAIL}/{complaintId}") { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId") ?: ""
            ComplaintDetailScreen(
                complaintId = complaintId,
                onBack = { navController.popBackStack() }
            )
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
