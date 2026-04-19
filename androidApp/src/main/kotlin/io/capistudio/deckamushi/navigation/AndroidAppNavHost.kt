package io.capistudio.deckamushi.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import io.capistudio.deckamushi.presentation.cards.CardListRoute
import io.capistudio.deckamushi.presentation.cards.CardsBrowserViewModel
import io.capistudio.deckamushi.presentation.collection.CollectionRoute
import kotlinx.coroutines.launch
import io.capistudio.deckamushi.presentation.components.DeckamushiTopAppBar
import io.capistudio.deckamushi.presentation.detail.CardDetailRoute
import io.capistudio.deckamushi.presentation.home.HomeScreen
import io.capistudio.deckamushi.presentation.sync.SyncRoute
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AndroidAppNavHost() {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val canGoBack = navController.previousBackStackEntry != null

    val title = when (route) {
        AndroidRoutes.CARDS -> "All Cards"
        AndroidRoutes.COLLECTION -> "My Collection"
        AndroidRoutes.SYNC -> "Sync Data"
        AndroidRoutes.SCAN -> "Scan"
        AndroidRoutes.SCAN_RESULTS -> "Scan Results"
        AndroidRoutes.CARD_DETAIL -> "Card Detail"
        else -> "Deckamushi"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DeckamushiTopAppBar(
                title = title,
                canGoBack = canGoBack,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = AndroidRoutes.HOME
            ) {
                composable(AndroidRoutes.HOME) {
                    HomeScreen(
                        onOpenCards = { navController.navigate(AndroidRoutes.CARDS) },
                        onOpenCollection = { navController.navigate(AndroidRoutes.COLLECTION) },
                        onOpenSync = { navController.navigate(AndroidRoutes.SYNC) },
                    )
                }

                composable(AndroidRoutes.CARDS) {
                    val vm: CardsBrowserViewModel = koinViewModel<CardsBrowserViewModel>()
                    CardListRoute(
                        showSnackbar = showSnackbar,
                        onNavigateToDetail = { id ->
                            navController.navigate(AndroidRoutes.cardDetail(id))
                        }
                    )
                }

                composable(AndroidRoutes.COLLECTION) {
                    CollectionRoute(
                        showSnackbar = showSnackbar,
                        onNavigateToDetail = { id ->
                            navController.navigate(AndroidRoutes.cardDetail(id))
                        }
                    )
                }

                composable(AndroidRoutes.SYNC) {
                    // When Sync finishes, return to Home (hub).
                    SyncRoute(showSnackbar = showSnackbar) {
                        navController.popBackStack()
                    }
                }

                composable(
                    route = AndroidRoutes.CARD_DETAIL,
                    arguments = listOf(navArgument(AndroidRoutes.ARG_CARD_ID) { type = NavType.StringType })
                ) { entry ->
                    val cardId = entry.arguments?.getString(AndroidRoutes.ARG_CARD_ID)
                        ?: return@composable

                    CardDetailRoute(
                        cardId = cardId,
                        showSnackbar = showSnackbar,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
