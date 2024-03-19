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
import com.meow.tsukinari.presentation.editor.add_chapter.AddChapterScreen
import com.meow.tsukinari.presentation.editor.add_chapter.AddChapterViewModel
import com.meow.tsukinari.presentation.my_fictions.MyFictionsScreen
import com.meow.tsukinari.presentation.my_fictions.MyFictionsViewModel
import com.meow.tsukinari.presentation.profile.ProfileScreen
import com.meow.tsukinari.presentation.profile.ProfileViewModel
import com.meow.tsukinari.presentation.reader.ReaderScreen
import com.meow.tsukinari.presentation.reader.ReaderViewModel
import com.meow.tsukinari.presentation.user_profile.UserProfileScreen
import com.meow.tsukinari.presentation.user_profile.UserProfileViewModel


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
    detailViewModel: DetailViewModel,
    profileViewModel: ProfileViewModel,
    addChapterViewModel: AddChapterViewModel,
    readerViewModel: ReaderViewModel,
    userProfileViewModel: UserProfileViewModel
) {
    NavHost(
        navController = navController, startDestination = NestedNav.Main.route
    ) {
        authGraph(navController, authViewModel)
        homeGraph(
            navController = navController,
            browseViewModel,
            editorViewModel,
            myFictionsViewModel,
            detailViewModel,
            profileViewModel,
            addChapterViewModel,
            readerViewModel,
            userProfileViewModel
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
    detailViewModel: DetailViewModel,
    profileViewModel: ProfileViewModel,
    addChapterViewModel: AddChapterViewModel,
    readerViewModel: ReaderViewModel,
    userProfileViewModel: UserProfileViewModel
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
            }, onNavToSignIn = {
                navController.navigate(AuthRoutes.SignIn.name)
            }, profileViewModel = profileViewModel)
        }
        composable(ExclusiveNav.MyFictions.route) {
            MyFictionsScreen(
                myFictionsViewModel = myFictionsViewModel,
                onNavToUpdatingPage = { fictionId ->
                    navController.navigate(
                        ExclusiveNav.Update.route + "?id=$fictionId"
                    ) {
                        launchSingleTop = true
                        popUpTo(ExclusiveNav.MyFictions.route)
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
            //pop up to the my fictions page

            UpdatingScreen(
                editorViewModel = editorViewModel,
                fictionId = entry.arguments?.getString("id") as String,
                onNavToAddingChapterPage = { id ->
                    navController.navigate(ExclusiveNav.AddChapter.route + "?id=$id")
                },
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
                    //nav up
                    navController.navigateUp()
                },
                onNavToReader = { chapterId ->
                    navController.navigate(ExclusiveNav.Reader.route + "?chapterId=$chapterId") {

                        launchSingleTop = true

                    }
                },
                onNavToProfile = { id ->
                    navController.navigate(ExclusiveNav.UserProfile.route + "?userId=$id") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = ExclusiveNav.Reader.route + "?chapterId={chapterId}",
            arguments = listOf(navArgument("chapterId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            ReaderScreen(
                viewModel = readerViewModel,
                fictionId = entry.arguments?.getString("chapterId") as String,
                onNavUp = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = ExclusiveNav.AddChapter.route + "?id={id}",
        ) { entry ->
            AddChapterScreen(
                addChapterViewModel = addChapterViewModel,
                onNavigateUp = {
                    navController.navigate(ExclusiveNav.Update.route)
                },
                fictionId = entry.arguments?.getString("id") as String
            )
        }

        //user profile page with user id
        composable(
            route = ExclusiveNav.UserProfile.route + "?userId={userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            UserProfileScreen(
                userProfileViewModel = userProfileViewModel,
                userId = entry.arguments?.getString("userId") as String,
                onNavToDetail = {
                    navController.navigate(ExclusiveNav.Detail.route + "?id=$it") {
                        launchSingleTop = true
                    }
                })
        }
    }
}


