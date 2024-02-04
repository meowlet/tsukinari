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
import com.meow.tsukinari.model.ExclusiveNav
import com.meow.tsukinari.model.HomeNav
import com.meow.tsukinari.model.NestedNav
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.authentication.ForgotPasswordScreen
import com.meow.tsukinari.presentation.authentication.SignInScreen
import com.meow.tsukinari.presentation.authentication.SignUpScreen
import com.meow.tsukinari.presentation.browse.BrowseScreen
import com.meow.tsukinari.presentation.browse.BrowseViewModel
import com.meow.tsukinari.presentation.detail.DetailScreen
import com.meow.tsukinari.presentation.detail.DetailViewModel
import com.meow.tsukinari.presentation.editor.EditorViewModel
import com.meow.tsukinari.presentation.editor.UpdatingScreen
import com.meow.tsukinari.presentation.editor.UploadingScreen
import com.meow.tsukinari.presentation.my_fictions.MyFictionsScreen
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel
import com.meow.tsukinari.presentation.profile.ProfileScreen


enum class AuthRoutes {
    SignIn, SignUp, ForgotPassword
}

enum class HomeRoutes {
    Browse, Explore, Notify, More,
    MyFictions, Upload, Update,
}


@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    browseViewModel: BrowseViewModel,
    authViewModel: AuthViewModel,
    editorViewModel: EditorViewModel,
    myFictionsViewModel: MyFictionsViewModel,
    detailViewModel: DetailViewModel
) {
    NavHost(
        navController = navController, startDestination = NestedNav.Authorization.route
    ) {
        authGraph(navController, authViewModel)
        homeGraph(
            navController = navController,
            browseViewModel,
            editorViewModel,
            myFictionsViewModel,
            detailViewModel
        )
    }


}

fun NavGraphBuilder.authGraph(
    navController: NavHostController, authViewModel: AuthViewModel
) {
    navigation(
        startDestination = AuthRoutes.SignIn.name, route = NestedNav.Authorization.route
    ) {
        composable(route = AuthRoutes.SignIn.name) {
            SignInScreen(onNavToHomePage = {
                navController.navigate(NestedNav.Main.route) {
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
                navController.navigate(HomeNav.Browse.route) {
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
    browseViewModel: BrowseViewModel,
    editorViewModel: EditorViewModel,
    myFictionsViewModel: MyFictionsViewModel,
    detailViewModel: DetailViewModel
) {
    navigation(
        startDestination = HomeNav.Browse.route,
        route = NestedNav.Main.route,
    ) {
        composable(HomeNav.Browse.route) {
            BrowseScreen(
                browseViewModel = browseViewModel,
                onNavToDetailPage = { fictionId ->
                    navController.navigate(ExclusiveNav.Detail.route + "?id=$fictionId") {

                        launchSingleTop = true

                    }
                })
        }
        composable(HomeNav.Profile.route) {
            ProfileScreen(onNavToMyFictions = {
                navController.navigate(ExclusiveNav.MyFictions.route)
            })
        }
        composable(ExclusiveNav.MyFictions.route) {
            MyFictionsScreen(
                myFictionsViewModel = myFictionsViewModel,
                onNavToUpdatingPage = { fictionId ->
                    navController.navigate(
                        ExclusiveNav.Update.route + "?id=$fictionId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavToSignInPage = {
                    navController.navigate(NestedNav.Authorization.route) {
                        launchSingleTop = true
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavToAddingPage = {
                    navController.navigate(ExclusiveNav.Upload.route)
                },
            )
        }

        composable(
            route = ExclusiveNav.Update.route + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            UpdatingScreen(
                editorViewModel = editorViewModel,
                fictionId = entry.arguments?.getString("id") as String,
                onNavigate = {
                    navController.navigate(ExclusiveNav.MyFictions.route)
                }
            )


        }

        composable(
            route = ExclusiveNav.Upload.route
        ) { entry ->
            UploadingScreen(
                onNavigate = { navController.navigate(ExclusiveNav.MyFictions.route) },
                editorViewModel = editorViewModel,
            )
        }

        composable(
            route = ExclusiveNav.Detail.route + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            DetailScreen(
                detailViewModel = detailViewModel,
                fictionId = entry.arguments?.getString("id") as String,
                onNavigate = {
                    navController.navigate(HomeNav.Browse.route)
                }
            )


        }


    }


}


