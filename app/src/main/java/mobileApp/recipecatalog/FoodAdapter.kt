package mobileApp.recipecatalog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mobileApp.recipecatalog.Room.Entities.ReceiptTable

class FoodAdapter(
    private val foods: MutableList<ReceiptTable>,
    private val onClick: (ReceiptTable) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_category_card, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.bind(food)
        holder.itemView.setOnClickListener { onClick(food) }
    }

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title : TextView = itemView.findViewById(R.id.food_title)
        private val image : ImageView = itemView.findViewById(R.id.food_img)
        fun bind(food : ReceiptTable){
            title.text = food.title

            image.setImageResource(food.imageID)
        }
    }

    override fun getItemCount() = foods.size

    fun updateData(newFoods : List<ReceiptTable>){
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
    }
}
