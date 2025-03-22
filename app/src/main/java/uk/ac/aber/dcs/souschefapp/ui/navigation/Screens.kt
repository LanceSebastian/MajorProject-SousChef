package uk.ac.aber.dcs.souschefapp.ui.navigation

sealed class Screen (val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object Recipes : Screen("recipes")
    object Profile : Screen("profile")
    object RecipePage  : Screen("recipe_page")
    object Auth : Screen("auth")
    object Product : Screen("product")

    /*
    Easier method to write arguments.
     */
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach{ arg ->
                append("/$arg")
            }
        }
    }
}