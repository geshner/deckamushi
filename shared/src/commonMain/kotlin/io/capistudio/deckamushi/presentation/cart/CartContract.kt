package io.capistudio.deckamushi.presentation.cart

import io.capistudio.deckamushi.domain.model.CartItemSummary
import org.koin.viewmodel.emptyState

object CartContract {

    data class State(
        val items: List<CartItemSummary> = emptyList(),
        val totalCount: Long = 0L,
        val showClearConfirmDialog: Boolean = false,
        val showCompletePurchaseDialog: Boolean = false,
        val isCompleting: Boolean = false,
    ) {
        val isCartEmpty: Boolean get() = items.isEmpty()
    }

    sealed interface Action {
        data object OnStart : Action
        data object RequestClearCart : Action
        data object ConfirmClearCart : Action
        data object RequestCompletePurchase : Action
        data object ConfirmCompletePurchase : Action
        data object DismissDialog : Action
        data object ScanCard : Action
        data class SetCartQuantity(val cardId: String, val quantity: Long) : Action
        data class RemoveItem(val cardId: String) : Action
        data class AddToCart(val cardId: String) : Action
    }

    sealed interface Effect {
        data object NavigateToScan : Effect
        data object PurchaseComplete : Effect
    }
}

