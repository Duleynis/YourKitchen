package mobileApp.recipecatalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.Room.DAO.StepDAO
import mobileApp.recipecatalog.Room.Entities.ReceiptSteps
import mobileApp.recipecatalog.Room.Entities.ReceiptTable
import mobileApp.recipecatalog.Room.Entities.StepReceiptTable

class RecipeViewModel (
    private val receiptDAO: ReceiptDAO,
    private val stepDAO: StepDAO
) : ViewModel() {

    val getAllReceipts : LiveData<List<ReceiptTable>> = receiptDAO.getAllReceipt()

    fun insertReceipt(receipt : ReceiptTable, onInserted: (Long) -> Unit){
        viewModelScope.launch {
            val receiptID = receiptDAO.insertReceipt(receipt)
            withContext(Dispatchers.Main){
                onInserted(receiptID)
            }
        }
    }

    fun deleteReceipt(receiptTitle : String){
        viewModelScope.launch {
            receiptDAO.removeReceipt(receiptTitle)
        }
    }

    fun insertReceiptSteps(steps: MutableList<StepReceiptTable>) {
        viewModelScope.launch {
            stepDAO.insertSteps(steps)
        }
    }

    fun getReceiptWithSteps(recipeID : Int) : LiveData<ReceiptSteps>{
        return receiptDAO.getReceiptWithSteps(recipeID)
    }

    fun getReceiptsByCategory(category : String): LiveData<List<ReceiptTable>>{
        return receiptDAO.getReceiptByCategory(category)
    }
}