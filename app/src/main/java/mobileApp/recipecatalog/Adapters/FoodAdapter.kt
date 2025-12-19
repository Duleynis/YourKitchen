package mobileApp.recipecatalog.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import mobileApp.recipecatalog.IReceiptDelete
import mobileApp.recipecatalog.R
import mobileApp.recipecatalog.Room.Entities.ReceiptTable
import java.io.File

class FoodAdapter(
    private val foods: MutableList<ReceiptTable>,
    private val onClick: (ReceiptTable) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    public var deleteReceiptListener : IReceiptDelete? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_category_card, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.bind(food)
        holder.itemView.setOnClickListener { onClick(food) }

        holder.itemView.findViewById<AppCompatImageButton>(R.id.deleteReceipt_btn).setOnClickListener {
            deleteReceiptListener?.deleteReceipt(food.title)
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteReceiptListener?.deleteReceipt(food.title)
                foods.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
            }
        }
    }

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title : TextView = itemView.findViewById(R.id.food_title)
        private val image : ImageView = itemView.findViewById(R.id.food_img)

        fun bind(food : ReceiptTable){
            title.text = food.title

            val file = File(food.imagePath)
            if(file.exists()){
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                image.setImageBitmap(bitmap)
            }
        }
    }

    override fun getItemCount() = foods.size

    fun updateData(newFoods : List<ReceiptTable>){
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
    }
}