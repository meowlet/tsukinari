package com.meow.tsukinari.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.authentication.ForgotPasswordScreen
import com.meow.tsukinari.presentation.authentication.SignInScreen
import com.meow.tsukinari.presentation.authentication.SignUpScreen
import com.meow.tsukinari.presentation.home.HomeScreen


enum class AuthRoutes {
    SignIn,
    SignUp,
    ForgotPassword
}

enum class HomeRoutes {
    Home,
    More
}


@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = AuthRoutes.SignIn.name) {
        composable(route = AuthRoutes.SignIn.name) {
            SignInScreen(
                onNavToHomePage = {
                    navController.navigate(HomeRoutes.Home.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                }, onNavToForgotPage = {
                    navController.navigate(AuthRoutes.ForgotPassword.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                        }
                    }
                }, authViewModel = authViewModel
            ) {
                navController.navigate(AuthRoutes.SignUp.name) {
                    launchSingleTop = true
                    popUpTo(AuthRoutes.SignIn.name) {
                    }
                }
            }
        }
        composable(route = AuthRoutes.SignUp.name) {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate(HomeRoutes.Home.name) {
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                }, authViewModel = authViewModel
            ) {
                navController.navigate(AuthRoutes.SignIn.name)
            }
        }
        composable(route = AuthRoutes.ForgotPassword.name) {
            ForgotPasswordScreen(
                authViewModel = authViewModel
            ) {
                navController.navigate(AuthRoutes.SignIn.name)
            }
        }
        composable(route = HomeRoutes.Home.name) {
            HomeScreen(authViewModel = authViewModel) {
                navController.navigate(AuthRoutes.SignIn.name)
            }
        }
    }
}