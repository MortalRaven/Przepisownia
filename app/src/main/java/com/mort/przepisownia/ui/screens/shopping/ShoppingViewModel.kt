package com.mort.przepisownia.ui.screens.shopping

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.Recipe
import com.mort.przepisownia.data.entities.ShoppingList
import com.mort.przepisownia.data.repository.ShoppingRepository
import com.mort.przepisownia.ui.screens.recipe.components.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository = Graph.shoppingRepository,
): ViewModel() {

    var listNameState by mutableStateOf("")
    var listCreatedState by mutableStateOf(0L)

    var isDbLoading by mutableStateOf(true)
    var isListLoading by mutableStateOf(true)

    private val _sortType = MutableStateFlow(SortType.DATE_ADDED_DESC)
    val sortType: StateFlow<SortType> = _sortType

    private val _allLists = shoppingRepository.getLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendingDeleteList = MutableStateFlow<ShoppingList?>(null)
    val pendingDeleteList: StateFlow<ShoppingList?> = _pendingDeleteList

    val filteredLists: StateFlow<List<ShoppingList>> = combine(
        _allLists,
        _sortType
    ) { lists, sortType ->
        lists.asSequence().sortedWith(
            when (sortType) {
                SortType.ALPHABET_ASC -> compareBy { it.name.lowercase() }
                SortType.ALPHABET_DESC -> compareByDescending { it.name.lowercase() }
                SortType.DATE_ADDED_ASC -> compareBy { it.createdAt }
                SortType.DATE_ADDED_DESC -> compareByDescending { it.createdAt }
                else -> compareBy { it.name }
            }
        ).toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _allLists.collect { lists ->
                if (isDbLoading) {
                    isDbLoading = false
                }
            }
        }
    }

    fun addList(
        list: ShoppingList,
        itemsInput: List<IngredientInput>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.addFullList(list, itemsInput)
        }
    }

    fun getListItems(listId: Long): Flow<ListWithItems> {
        return shoppingRepository.getListItems(listId)
    }

    fun deleteList(list: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.deleteList(list)
        }
    }

    fun updateList(
        list: ShoppingList,
        itemsInput: List<IngredientInput>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.updateRecipe(list, itemsInput)
        }
    }

    fun setPendingDeleteList(list: ShoppingList) {
        _pendingDeleteList.value = list
    }

    fun clearPendingDeleteList() {
        _pendingDeleteList.value = null
    }

    fun onListNameChange(newString: String) {
        listNameState = newString
    }
}