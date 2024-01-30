package com.meow.tsukinari.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.authentication.ForgotPasswordScreen
import com.meow.tsukinari.presentation.authentication.SignInScreen
import com.meow.tsukinari.presentation.authentication.SignUpScreen
import com.meow.tsukinari.presentation.editor.EditorViewModel
import com.meow.tsukinari.presentation.editor.UpdatingScreen
import com.meow.tsukinari.presentation.editor.UploadingScreen
import com.meow.tsukinari.presentation.my_fictions.MyFictionsScreen
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel


enum class AuthRoutes {
    SignIn, SignUp, ForgotPassword
}

enum class HomeRoutes {
    MyFictions, Upload, Update,
}

enum class NestedRoutes {
    Main, Auth
}


@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    editorViewModel: EditorViewModel,
    myFictionsViewModel: MyFictionsViewModel
) {
    NavHost(
        navController = navController, startDestination = NestedRoutes.Main.name
    ) {
        authGraph(navController, authViewModel)
        homeGraph(
            navController = navController, editorViewModel, myFictionsViewModel
        )
    }


}

fun NavGraphBuilder.authGraph(
    navController: NavHostController, authViewModel: AuthViewModel
) {
    navigation(
        startDestination = AuthRoutes.SignIn.name, route = NestedRoutes.Auth.name
    ) {
        composable(route = AuthRoutes.SignIn.name) {
            SignInScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    launchSingleTop = true
                    popUpTo(route = AuthRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }, onNavToForgotPage = {
                navController.navigate(AuthRoutes.ForgotPassword.name) {
                    launchSingleTop = true
                    popUpTo(AuthRoutes.SignIn.name) {}
                }
            }, onNavToSignUpPage = {

                navController.navigate(AuthRoutes.SignUp.name) {
                    launchSingleTop = true
                    popUpTo(AuthRoutes.SignIn.name) {
                    }
                }
            }, authViewModel = authViewModel
            )
        }

        composable(route = AuthRoutes.SignUp.name) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(HomeRoutes.MyFictions.name) {
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
                    popUpTo(AuthRoutes.SignIn.name) {}
                }
            }, authViewModel = authViewModel)
        }
        composable(route = AuthRoutes.ForgotPassword.name) {
            ForgotPasswordScreen(
                onNavToSignInPage = {
                    navController.navigate(AuthRoutes.SignIn.name) {
                        popUpTo(AuthRoutes.SignIn.name) {
                            inclusive = true
                        }
                    }
                }, authViewModel = authViewModel
            )
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    editorViewModel: EditorViewModel,
    myFictionsViewModel: MyFictionsViewModel
) {
    navigation(
        startDestination = HomeRoutes.MyFictions.name,
        route = NestedRoutes.Main.name,
    ) {
        composable(HomeRoutes.MyFictions.name) {
            MyFictionsScreen(
                myFictionsViewModel = myFictionsViewModel,
                onNavToUpdatingPage = { fictionId ->
                    navController.navigate(
                        HomeRoutes.Update.name + "?id=$fictionId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavToSignInPage = {
                    navController.navigate(NestedRoutes.Auth.name) {
                        launchSingleTop = true
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavToAddingPage = {
                    navController.navigate(HomeRoutes.Upload.name)
                },
            )
        }

        composable(
            route = HomeRoutes.Update.name + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            UpdatingScreen(
                editorViewModel = editorViewModel,
                fictionId = entry.arguments?.getString("id") as String,
                onNavigate = {
                    navController.navigate(HomeRoutes.MyFictions.name)
                }
            )


        }

        composable(
            route = HomeRoutes.Upload.name
        ) { entry ->
            UploadingScreen(
                onNavigate = { navController.navigate(HomeRoutes.MyFictions.name) },
                editorViewModel = editorViewModel,
            )
        }


    }


}


