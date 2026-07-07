package com.goran.aitouristagent.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.goran.aitouristagent.ui.budget.BudgetScreen
import com.goran.aitouristagent.ui.chat.ChatScreen
import com.goran.aitouristagent.ui.itinerary.ItineraryScreen
import com.goran.aitouristagent.ui.settings.SettingsScreen
import com.goran.aitouristagent.ui.trips.TripsScreen

private const val ROUTE_TRIPS = "trips"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_ITINERARY = "trip/{tripId}"
private const val ROUTE_BUDGET = "trip/{tripId}/budget"
private const val ROUTE_CHAT = "trip/{tripId}/chat"
private const val ARG_TRIP_ID = "tripId"

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = ROUTE_TRIPS) {
        composable(ROUTE_TRIPS) {
            TripsScreen(
                onOpenTrip = { tripId -> navController.navigate("trip/$tripId") },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
            )
        }
        composable(ROUTE_ITINERARY) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(ARG_TRIP_ID).orEmpty()
            ItineraryScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenBudget = { navController.navigate("trip/$tripId/budget") },
                onOpenChat = { navController.navigate("trip/$tripId/chat") },
            )
        }
        composable(ROUTE_BUDGET) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(ARG_TRIP_ID).orEmpty()
            BudgetScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenItinerary = { navController.navigate("trip/$tripId") },
                onOpenChat = { navController.navigate("trip/$tripId/chat") },
            )
        }
        composable(ROUTE_CHAT) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(ARG_TRIP_ID).orEmpty()
            ChatScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenItinerary = { navController.navigate("trip/$tripId") },
                onOpenBudget = { navController.navigate("trip/$tripId/budget") },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
