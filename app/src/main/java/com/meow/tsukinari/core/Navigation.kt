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
import com.meow.tsukinari.presentation.my_fictions.MyFictionsScreen


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
                }, onNavToSignUpPage = {
                    authViewModel.clearSignUpForm()
                    navController.navigate(AuthRoutes.SignUp.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                        }
                    }
                }, onNavToForgotPage = {
                    navController.navigate(AuthRoutes.ForgotPassword.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                        }
                    }
                }, authViewModel = authViewModel
            )
        }
        composable(route = AuthRoutes.SignUp.name) {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate(HomeRoutes.Home.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                }, onNavToSignInPage = {
                    navController.navigate(AuthRoutes.SignIn.name) {
                        launchSingleTop = true
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                }, onNavToForgotPage = {
                    navController.navigate(AuthRoutes.ForgotPassword.name) {
                        popUpTo(AuthRoutes.SignIn.name) {
                        }
                    }
                }, authViewModel = authViewModel
            )
        }
        composable(route = AuthRoutes.ForgotPassword.name) {
            ForgotPasswordScreen(
                onNavToSignInPage = {
                    navController.navigate(AuthRoutes.SignIn.name) {
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable(route = HomeRoutes.Home.name) {
            MyFictionsScreen(onNavToSignInPage = {
                navController.navigate(AuthRoutes.SignIn.name) {
                    popUpTo(HomeRoutes.Home.name) {
                        inclusive = true
                    }
                }
            }, authViewModel = authViewModel)
        }
    }
}
