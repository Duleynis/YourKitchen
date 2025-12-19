package mobileApp.recipecatalog.Room.DAO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import mobileApp.recipecatalog.Room.Entities.StepReceiptTable

@Dao
interface StepDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps : List<StepReceiptTable>)
}