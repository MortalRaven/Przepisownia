package com.mort.przepisownia.ui.screens.shopping

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mort.przepisownia.R
import com.mort.przepisownia.data.Graph
import com.mort.przepisownia.data.entities.IngredientInput
import com.mort.przepisownia.data.entities.ListWithItems
import com.mort.przepisownia.data.entities.ShoppingList
import com.mort.przepisownia.data.repository.ShoppingRepository
import com.mort.przepisownia.model.EditMode
import com.mort.przepisownia.ui.screens.recipe.components.IngredientDialogUiState
import com.mort.przepisownia.ui.screens.recipe.components.SortType
import com.mort.przepisownia.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository = Graph.shoppingRepository,
): ViewModel() {

    private val _events = MutableSharedFlow<ShoppingListEvent>()
    val events = _events.asSharedFlow()

    private var initialized = false

    private var listId by mutableLongStateOf(0L)
    var listNameState by mutableStateOf("")
    private var listCreatedState by mutableLongStateOf(0L)
    val ingredients = mutableStateListOf<IngredientInput>()

    var isDbLoading by mutableStateOf(true)
    private var isListLoading by mutableStateOf(true)

    var ingredientDialogState by mutableStateOf(IngredientDialogUiState())

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

    fun initializeList(
        id: Long,
        mode: EditMode,
        list: ListWithItems?
    ) {
        if (id != listId) initialized = false
        if (initialized) return

        when (mode) {
            EditMode.ADD -> {
                clearListForm()
            }

            EditMode.EDIT -> {
                if (list == null) return

                if (list.shoppingList.id == 0L) return

                listId = list.shoppingList.id
                listNameState = list.shoppingList.name
                listCreatedState = list.shoppingList.createdAt

                ingredients.clear()
                ingredients.addAll(
                    list.shoppingItems.map {
                        IngredientInput(
                            name = it.name,
                            quantity =  it.quantity,
                            unit = it.unit
                        )
                    }
                )
                isListLoading = false
            }
        }
        initialized = true
    }

    private fun clearListForm() {
        listId = 0L
        listNameState = ""
        listCreatedState = 0L
        ingredients.clear()
        isListLoading = true
        initialized = false
    }

    fun saveList(
        mode: EditMode
    ) {
        viewModelScope.launch {
            if (ingredients.isEmpty()) {
                _events.emit(ShoppingListEvent.ShowSnackbar(R.string.warning_empty_fields))
                return@launch
            }

            if (mode == EditMode.EDIT) {
                updateList(
                    list = ShoppingList(
                        id = listId,
                        name = listNameState.trim(),
                        createdAt = listCreatedState,
                        lastEdited = System.currentTimeMillis()
                    ),
                    itemsInput = ingredients.toList()
                )
                _events.emit(ShoppingListEvent.ShowSnackbar(R.string.list_updated))
                _events.emit(ShoppingListEvent.NavigateBack)
            } else {
                addList(
                    list = ShoppingList(
                        name = if (listNameState.isEmpty()) formatDate(System.currentTimeMillis()) else listNameState.trim(),
                    ),
                    itemsInput = ingredients.toList()
                )
                _events.emit(ShoppingListEvent.ShowSnackbar(R.string.list_added))
                _events.emit(ShoppingListEvent.NavigateBack)
            }
        }
    }

    private fun addList(
        list: ShoppingList,
        itemsInput: List<IngredientInput>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.addFullList(list, itemsInput)
        }
    }

    private fun updateList(
        list: ShoppingList,
        itemsInput: List<IngredientInput>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.updateRecipe(list, itemsInput)
        }
    }

    fun getListById(id: Long): Flow<ShoppingList> {
        return shoppingRepository.getListByID(id)
    }

    fun getListItems(listId: Long): Flow<ListWithItems> {
        return shoppingRepository.getListItems(listId)
    }

    fun deleteList(list: ShoppingList) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.deleteList(list)
        }
    }

    fun updateItemChecked(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.updateItemChecked(id)
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

    fun saveIngredient(ingredient: IngredientInput) {
        when (ingredientDialogState.mode) {
            EditMode.ADD -> {
                ingredients.add(ingredient)
            }

            EditMode.EDIT -> {
                ingredients[ingredientDialogState.editIndex] = ingredient
            }
        }
        closeIngredientDialog()
    }

    fun openIngredientDialog(index: Int = -1) {
        ingredientDialogState = if (index != -1) {
            IngredientDialogUiState(
                isVisible = true,
                mode = EditMode.EDIT,
                editIndex = index
            )
        } else {
            IngredientDialogUiState(
                isVisible = true,
                mode = EditMode.ADD
            )
        }
    }

    fun closeIngredientDialog() {
        ingredientDialogState = ingredientDialogState.copy(isVisible = false)
    }
}

sealed class ShoppingListEvent {
    data class ShowSnackbar(val message: Int): ShoppingListEvent()
    //data class NavigateToList(val id: Long): ShoppingListEvent()
    object NavigateBack: ShoppingListEvent()
}