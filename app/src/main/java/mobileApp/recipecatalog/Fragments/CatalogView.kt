package mobileApp.recipecatalog.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import mobileApp.recipecatalog.Adapters.FoodAdapter
import mobileApp.recipecatalog.IReceiptDelete
import mobileApp.recipecatalog.R
import mobileApp.recipecatalog.RecipeViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CatalogView : Fragment(), IReceiptDelete {
    private lateinit var root: View
    private lateinit var searchBar: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var add_receipt_btn: Button
    private val viewModel: RecipeViewModel by activityViewModel()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_receipts_catalog, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()

        adapter = FoodAdapter(mutableListOf()) { receipt ->
            val bundle = bundleOf("receiptID" to receipt.foodID)
            findNavController().navigate(R.id.action_Catalog_Fragment_to_AddReceipt_Fragment, bundle)
        }
        recyclerView.adapter = adapter
        adapter.deleteReceiptListener = this

        // Подписка на результаты поиска
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                adapter.updateData(results)
            }
        }

        // Подписка на searchQuery, чтобы EditText синхронизировался
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchQuery.collect { query ->
                if (searchBar.text.toString() != query) {
                    searchBar.setText(query)
                    searchBar.setSelection(query.length)
                }
            }
        }

        // Обработка ввода текста пользователем
        searchBar.addTextChangedListener { editable ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setSearchQuery(editable.toString())
            }
        }

        // Кнопка добавления рецепта
        add_receipt_btn.setOnClickListener {
            findNavController().navigate(R.id.action_Catalog_Fragment_to_AddReceipt_Fragment)
        }

        // Spinner категорий
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Сброс фильтра категории
                    viewModel.setCategory(null)
                } else {
                    viewModel.setCategory(categorySpinner.selectedItem.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Сохраняем текст поисковой строки при уходе с экрана
    override fun onPause() {
        super.onPause()
        viewModel.setSearchQuery(searchBar.text.toString())
    }

    //Настраиваем выпадающий список с категориями
    fun SetSpinner() {
        val categories = listOf(
            "Выберите категорию блюда",
            "Салаты", "Первые блюда", "Вторые блюда", "Гарниры", "Десерты", "Напитки"
        )

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
    }

    override fun deleteReceipt(receiptTitle : String) {
        viewModel.deleteReceipt(receiptTitle)
    }

    fun initializeComponents() {
        searchBar = root.findViewById(R.id.search_food)
        categorySpinner = root.findViewById(R.id.categorySpinner)
        SetSpinner()

        recyclerView = root.findViewById(R.id.food_catalog_RecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.requireActivity(), 2)

        add_receipt_btn = root.findViewById(R.id.add_receipt_btn)
    }
}