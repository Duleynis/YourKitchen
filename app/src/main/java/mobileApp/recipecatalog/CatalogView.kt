package mobileApp.recipecatalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogView : Fragment() {
    private lateinit var root : View
    private lateinit var recyclerView: RecyclerView
    private lateinit var categorySpinner: Spinner
    private val viewModel : ViewModel by viewModel()
    private lateinit var adapter : FoodAdapter

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

        }

        recyclerView.adapter = adapter

        viewModel.getAllReceipts.observe(viewLifecycleOwner) { foods ->
            adapter.updateData(foods)
        }
    }

    //Настраиваем выпадающий список с категориями
    fun SetSpinner() {
        val categories = listOf(
            "Выберите категорию",
            "Салаты", "Первые блюда", "Вторые блюда", "Гарниры", "Десерты","Напитки"
        )

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
    }

    fun initializeComponents(){
        recyclerView = root.findViewById(R.id.food_catalog_RecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.requireActivity(), 2)

        categorySpinner = root.findViewById(R.id.categorySpinner)
        SetSpinner()
    }
}