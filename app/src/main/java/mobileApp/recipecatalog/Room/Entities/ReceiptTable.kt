package mobileApp.recipecatalog.Room.Entities
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Receipts",
    indices = [Index(value = ["title"], unique = true)]
)
data class ReceiptTable (
    @PrimaryKey(autoGenerate = true) val foodID : Int = 0,
    val title : String,
    val category: String,
    val imagePath : String,
    val ingredientsDescription : String
)