package mobileApp.recipecatalog.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.Room.Entities.ReceiptTable

@Database(
    entities = [ReceiptTable::class],
    version = 1
)

abstract class AppDataBase : RoomDatabase(){
    abstract fun receiptDAO() : ReceiptDAO
}