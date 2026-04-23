package io.capistudio.deckamushi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.capistudio.deckamushi.presentation.cards.CardListRoute
import io.capistudio.deckamushi.presentation.cards.CardListViewModel
import io.capistudio.deckamushi.presentation.collection.CollectionRoute
import io.capistudio.deckamushi.presentation.components.DeckamushiTopAppBar
import io.capistudio.deckamushi.presentation.detail.CardDetailRoute
import io.capistudio.deckamushi.presentation.home.HomeScreen
import io.capistudio.deckamushi.presentation.navigation.Screen
import io.capistudio.deckamushi.presentation.scan.ScanResultsRoute
import io.capistudio.deckamushi.presentation.scan.ScanRoute
import io.capistudio.deckamushi.presentation.sync.SyncRoute
import io.capistudio.deckamushi.presentation.theme.DeckamushiTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


/**
 * Shared app shell for Deckamushi.
 *
 * Owns the typed navigation graph, top app bar state, and global snackbar host. It also keeps
 * the scan-specific back override used by `CardDetailRoute` so app-bar back can participate in
 * the same special back behavior as system back gestures.
 */
@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination
    val canGoBack = navController.previousBackStackEntry != null

    val title = when {
        currentDestination?.hasRoute<Screen.Home>() == true -> "Deckamushi"
        currentDestination?.hasRoute<Screen.CardList>() == true -> "All Cards"
        currentDestination?.hasRoute<Screen.Collection>() == true -> "My Collection"
        currentDestination?.hasRoute<Screen.Sync>() == true -> "Sync Data"
        currentDestination?.hasRoute<Screen.CardDetail>() == true -> "Card Detail"
        currentDestination?.hasRoute<Screen.Scanner>() == true -> ""
        currentDestination?.hasRoute<Screen.ScanResults>() == true -> "Pick your card"
        else -> "Deckamushi"
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }
    // Allows a route to temporarily replace default app-bar back behavior without moving
    // navigation logic into the top app bar itself. Currently used by CardDetail scan flow.
    val backOverride = remember { mutableStateOf<(() -> Unit)?>(null)  }

    DeckamushiTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                DeckamushiTopAppBar(
                    title = title,
                    canGoBack = canGoBack,
                    onBackClick = {
                        // Prefer route-specific back handling when present; otherwise perform the
                        // normal one-step back stack pop.
                        backOverride.value?.invoke() ?: navController.popBackStack()
                    },
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home
                ) {
                    composable<Screen.Home> {
                        HomeScreen(
                            onOpenCards = { navController.navigate(Screen.CardList) },
                            onOpenCollection = { navController.navigate(Screen.Collection) },
                            onOpenSync = { navController.navigate(Screen.Sync) },
                            onOpenScanner = { navController.navigate(Screen.Scanner) },
                        )
                    }

                    composable<Screen.CardList> {
                        CardListRoute(
                            showSnackbar = showSnackbar,
                            onNavigateToDetail = { id ->
                                navController.navigate(Screen.CardDetail(id))
                            }
                        )
                    }

                    composable<Screen.Collection> {
                        CollectionRoute(
                            showSnackbar = showSnackbar,
                            onNavigateToDetail = { id ->
                                navController.navigate(Screen.CardDetail(id))
                            }
                        )
                    }

                    composable<Screen.Sync> {
                        // When Sync finishes, return to Home (hub).
                        SyncRoute(showSnackbar = showSnackbar) {
                            navController.popBackStack()
                        }
                    }

                    composable<Screen.CardDetail> { backStackEntry ->
                        val detail = backStackEntry.toRoute<Screen.CardDetail>()
                        CardDetailRoute(
                            cardId = detail.id,
                            fromScan = detail.fromScan,
                            showSnackbar = showSnackbar,
                            // CardDetail registers a temporary back handler here so app-bar back
                            // follows the same scan-aware rules as gesture/system back.
                            onRegisterBackOverride = { handler -> backOverride.value = handler },
                            onBack = { navController.popBackStack() },
                            onBackSkipScanResults = {
                                // When detail came from scan flow and quantity changed, return
                                // directly to Scanner instead of re-showing ScanResults.
                                if (!navController.popBackStack<Screen.ScanResults>(inclusive = true)) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable<Screen.Scanner> {
                        ScanRoute(
                            showSnackbar = showSnackbar,
                            onNavigateToDetail = { id ->
                                navController.navigate(Screen.CardDetail(id, fromScan = true))
                            },
                            onNavigateToResults = { baseId ->
                                navController.navigate(Screen.ScanResults(baseId))
                            }
                        )
                    }

                    composable<Screen.ScanResults> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screen.ScanResults>()
                        ScanResultsRoute(
                            baseId = route.baseId,
                            onNavigateToDetail = { id ->
                                navController.navigate(Screen.CardDetail(id, fromScan = true))
                            }
                        )
                    }
                }
            }
        }
    }
}
