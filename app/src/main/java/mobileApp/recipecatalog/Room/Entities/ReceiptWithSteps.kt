package mobileApp.recipecatalog.Room.Entities

import androidx.room.Embedded
import androidx.room.Relation

data class ReceiptSteps (
    @Embedded val receipt : ReceiptTable,
    @Relation(
        parentColumn = "foodID",
        entityColumn = "recipeID"
    )
    val steps: List<StepReceiptTable>
)