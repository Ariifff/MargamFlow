package com.arif.margamflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arif.margamflow.ui.screens.ForgotPasswordScreen
import com.arif.margamflow.ui.screens.Home
import com.arif.margamflow.ui.screens.LoginScreen
import com.arif.margamflow.ui.screens.PasswordRecoveryScreen
import com.arif.margamflow.ui.screens.SignUpScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Recovery : Screen("recovery/{username}") {
        fun createRoute(username: String) = "recovery/$username"
    }
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onSignIn = { username, password ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onForgetClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUp = { username, password, name, email ->
                    navController.navigate(Screen.Recovery.createRoute(username)) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                checkUsernameAvailability = { username ->
                    val taken = listOf("admin", "user", "test", "john", "jane")
                    !taken.contains(username.lowercase())
                }
            )
        }


        composable(Screen.Recovery.route) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""

            PasswordRecoveryScreen(
                username = username,
                onSubmit = { answer1, answer2, answer3 ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Recovery.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Screen.Home.route) {
            Home()
        }

    }
}
