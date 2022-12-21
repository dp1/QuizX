package com.labmacc.quizx

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("Splash")
    object Home : NavRoutes("Ranking")
    object Login : NavRoutes("Login")
    object ShowQuiz : NavRoutes("ShowQuiz")

}