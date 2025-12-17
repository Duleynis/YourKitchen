package mobileApp.recipecatalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReceiptCatalogView : Fragment() {
    private lateinit var root : View
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        val categories = listOf(
            Category("Салаты", imageRes = R.drawable.salads),
            Category("Первые блюда", imageRes = R.drawable.first),
            Category("Вторые блюда", imageRes = R.drawable.second),
            Category("Гарниры", imageRes = R.drawable.garnish),
            Category("Десерты", imageRes = R.drawable.dessert),
            Category("Напитки", imageRes = R.drawable.drinks),
        )

        recyclerView.adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this.requireActivity(), "Выбрано: ${category.name}", Toast.LENGTH_SHORT).show()
        }
    }
    fun initializeComponents(){
        recyclerView = root.findViewById(R.id.food_category_RecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.requireActivity(), 2)
    }
}