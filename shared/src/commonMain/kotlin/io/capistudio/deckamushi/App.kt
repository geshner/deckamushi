package io.capistudio.deckamushi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import io.capistudio.deckamushi.presentation.sync.SyncRoute
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


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
        else -> "Deckamushi"
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                DeckamushiTopAppBar(
                    title = title,
                    canGoBack = canGoBack,
                    onBackClick = { navController.popBackStack() },
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
                        )
                    }

                    composable<Screen.CardList> {
                        koinViewModel<CardListViewModel>()
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
                            showSnackbar = showSnackbar,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
