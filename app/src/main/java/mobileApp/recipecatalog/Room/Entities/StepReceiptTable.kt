package mobileApp.recipecatalog.Room.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Steps",
    foreignKeys = [ForeignKey(
        entity = ReceiptTable::class,
        parentColumns = ["foodID"],
        childColumns = ["recipeID"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class StepReceiptTable (
    @PrimaryKey(autoGenerate = true) val stepID : Int = 0,
    val recipeID: Long,
    val stepNumber: Int,
    var stepDescription: String
)