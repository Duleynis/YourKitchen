import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mobileApp.recipecatalog.R

class CategoryAdapter(
    private val categories: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_category_card, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
        holder.itemView.setOnClickListener { onClick(category) }
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryTitle : TextView = itemView.findViewById(R.id.categoryTitle)
        private val categoryImage : ImageView = itemView.findViewById(R.id.food_category)
        fun bind(foodCategory : Category){
            categoryTitle.text = foodCategory.name
            categoryImage.setImageResource(foodCategory.imageRes)
        }
    }

    override fun getItemCount() = categories.size
}

data class Category(
    val name: String,
    val imageRes: Int
)
