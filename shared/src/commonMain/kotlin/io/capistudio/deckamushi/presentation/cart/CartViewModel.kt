package io.capistudio.deckamushi.presentation.cart

import androidx.lifecycle.viewModelScope
import io.capistudio.deckamushi.domain.usecase.AddToCartUseCase
import io.capistudio.deckamushi.domain.usecase.ClearCartUseCase
import io.capistudio.deckamushi.domain.usecase.CompletePurchaseUseCase
import io.capistudio.deckamushi.domain.usecase.GetCartItemsUseCase
import io.capistudio.deckamushi.domain.usecase.GetCartTotalCountUseCase
import io.capistudio.deckamushi.domain.usecase.RemoveFromCartUseCase
import io.capistudio.deckamushi.domain.usecase.SetCartQuantityUseCase
import io.capistudio.deckamushi.presentation.cart.CartContract.Effect
import io.capistudio.deckamushi.presentation.mvi.Mvi
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartItems: GetCartItemsUseCase,
    private val addCardToCart: AddToCartUseCase,
    private val getCartTotalCount: GetCartTotalCountUseCase,
    private val setCartQuantity: SetCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val clearCart: ClearCartUseCase,
    private val completePurchase: CompletePurchaseUseCase,
) : Mvi<CartContract.State, CartContract.Action, Effect>(
    initialState = CartContract.State()
) {
    override suspend fun handleAction(action: CartContract.Action) {
        when (action) {
            CartContract.Action.OnStart -> observeCartItems()
            is CartContract.Action.AddToCart -> viewModelScope.launch() { addCardToCart(action.cardId) }
            is CartContract.Action.SetCartQuantity -> action.let {
                updateQuantity(it.cardId, it.quantity)
            }
            is CartContract.Action.RemoveItem -> removeItem(action.cardId)
            CartContract.Action.ConfirmClearCart -> clearCartContent()
            CartContract.Action.ConfirmCompletePurchase -> purchase()
            CartContract.Action.DismissDialog -> setState { copy(
                showClearConfirmDialog = false,
                showCompletePurchaseDialog = false,
            )}
            CartContract.Action.RequestClearCart -> {
                setState { copy(
                    showClearConfirmDialog = true
                ) }
            }
            CartContract.Action.RequestCompletePurchase -> {
                setState { copy(
                    showCompletePurchaseDialog = true
                ) }
            }
            CartContract.Action.ScanCard -> emitEffect(Effect.NavigateToScan)
        }
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            getCartItems().collect { items ->
                setState { copy (
                    items = items,
                    totalCount = items.sumOf { it.cartQuantity }
                )}
            }
        }
    }

    private fun updateQuantity(cardId: String, quantity: Long) {
        viewModelScope.launch {
            if (quantity <= 0L) {
                removeItem(cardId)
            } else {
                setCartQuantity(cardId, quantity)
            }
        }
    }

    private fun removeItem(cardId: String){
        viewModelScope.launch {
            removeFromCart(cardId)
        }
    }

    private fun clearCartContent() {
        viewModelScope.launch {
            clearCart()
            setState { copy(showClearConfirmDialog = false) }
        }
    }

    private fun purchase() {
        viewModelScope.launch {
            setState { copy(isCompleting = true, showCompletePurchaseDialog = false) }
            completePurchase()
            emitEffect(Effect.PurchaseComplete)
        }
    }
}