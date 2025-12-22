package mobileApp.recipecatalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.Room.DAO.StepDAO
import mobileApp.recipecatalog.Room.Entities.ReceiptSteps
import mobileApp.recipecatalog.Room.Entities.ReceiptTable
import mobileApp.recipecatalog.Room.Entities.StepReceiptTable
import kotlinx.coroutines.flow.combine

class RecipeViewModel(
    private val receiptDAO: ReceiptDAO,
    private val stepDAO: StepDAO
) : ViewModel() {

    // Текст поисковой строки
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> get() = _categoryFilter

    // Поток результатов поиска + фильтра категории
    val searchResults: StateFlow<List<ReceiptTable>> = combine(
        _searchQuery,
        _categoryFilter
    ) { query, category ->
        query to category
    }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, category) ->
            when {
                !category.isNullOrEmpty() && query.isNotBlank() ->
                    receiptDAO.getReceiptByCategoryAndQuery(category, "%$query%")
                !category.isNullOrEmpty() ->
                    receiptDAO.getReceiptByCategory(category)
                query.isBlank() ->
                    receiptDAO.getAllReceipt()
                else ->
                    receiptDAO.getReceiptsByQuery("%$query%")
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String?) {
        _categoryFilter.value = category
    }

    fun insertReceipt(receipt: ReceiptTable, onInserted: (Long) -> Unit) {
        viewModelScope.launch {
            val receiptID = receiptDAO.insertReceipt(receipt)
            withContext(Dispatchers.Main) { onInserted(receiptID) }
        }
    }

    fun updateReceipt(receipt: ReceiptTable) {
        viewModelScope.launch {
            receiptDAO.updateReceipt(receipt)
        }
    }

    fun deleteReceipt(receiptTitle: String) {
        viewModelScope.launch {
            receiptDAO.removeReceipt(receiptTitle)
        }
    }

    fun insertReceiptSteps(steps: List<StepReceiptTable>) {
        viewModelScope.launch {
            stepDAO.insertSteps(steps)
        }
    }

    fun deleteStepsByRecipeId(receiptID : Long) {
        viewModelScope.launch {
            stepDAO.deleteStepsByRecipeId(receiptID)
        }
    }

    fun getReceiptWithSteps(recipeID: Int): LiveData<ReceiptSteps> {
        return receiptDAO.getReceiptWithSteps(recipeID)
    }
}
