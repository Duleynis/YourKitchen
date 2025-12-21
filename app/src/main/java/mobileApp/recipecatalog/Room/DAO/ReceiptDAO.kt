package mobileApp.recipecatalog.Room.DAO
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import mobileApp.recipecatalog.Room.Entities.ReceiptSteps
import mobileApp.recipecatalog.Room.Entities.ReceiptTable

@Dao
interface ReceiptDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt : ReceiptTable): Long

    @Query("DELETE FROM Receipts WHERE title = :receiptTitle")
    suspend fun removeReceipt(receiptTitle : String)

    @Query("SELECT * FROM Receipts WHERE LOWER(title) LIKE LOWER(:query)")
    fun getReceiptsByQuery(query : String) : Flow<List<ReceiptTable>>

    @Query("SELECT * FROM RECEIPTS WHERE category = :category AND title LIKE LOWER(:query)")
    fun getReceiptByCategoryAndQuery(category: String, query: String): Flow<List<ReceiptTable>>

    @Query ("SELECT * FROM Receipts")
    fun getAllReceipt() : Flow<List<ReceiptTable>>

    @Transaction
    @Query("SELECT * FROM Receipts WHERE foodID = :id")
    fun getReceiptWithSteps(id: Int): LiveData<ReceiptSteps>

    @Query ("SELECT * FROM RECEIPTS WHERE category = :category")
    fun getReceiptByCategory(category : String) : Flow<List<ReceiptTable>>
}