package mobileApp.recipecatalog.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.Room.DAO.StepDAO
import mobileApp.recipecatalog.Room.Entities.ReceiptTable
import mobileApp.recipecatalog.Room.Entities.StepReceiptTable

@Database(
    entities = [ReceiptTable::class, StepReceiptTable::class],
    version = 1
)

abstract class AppDataBase : RoomDatabase(){
    abstract fun receiptDAO() : ReceiptDAO

    abstract fun stepDAO() : StepDAO
}