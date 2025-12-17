package mobileApp.recipecatalog.Room.DAO
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mobileApp.recipecatalog.Room.Entities.ReceiptTable

@Dao
interface ReceiptDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt : ReceiptTable)

    @Delete
    suspend fun removeReceipt(receipt : ReceiptTable)

    @Query ("SELECT * FROM Receipts")
    suspend fun getAllReceipt() : MutableList<ReceiptTable>
}