package com.meow.tsukinari.presentation.my_fictions

import androidx.compose.runtime.Composable

@Composable
fun MyFictionsScreen(
    myFictionsViewModel: MyFictionsViewModel? = null,
    onNavToSignInPage: () -> Unit,
    onNavToAddingPage: () -> Unit,
    onNavToUpdatingPage: () -> Unit
) {

}


//    Surface {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            val context = LocalContext.current
//            Text(text = "Home Screen")
//            Button(onClick = {
//                myFictionsViewModel?.signOut()
//                onNavToSignInPage.invoke()
//            }) {
//                Text(text = "Sign out")
//            }
//        }
//
//    }
