package mobileApp.recipecatalog

import Category
import CategoryAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeComponents()

        val categories = listOf(
            Category("Салаты", imageRes = R.drawable.salads_1),
            Category("Первые блюда", imageRes = R.drawable.first),
            Category("Вторые блюда", imageRes = R.drawable.second_1),
            Category("Гарниры", imageRes = R.drawable.garnish_1),
            Category("Десерты", imageRes = R.drawable.dessert_1),
            Category("Напитки", imageRes = R.drawable.drinks),
        )

        recyclerView.adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Выбрано: ${category.name}", Toast.LENGTH_SHORT).show()
        }
    }

    fun initializeComponents(){
        recyclerView = findViewById(R.id.food_category_RecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }
}