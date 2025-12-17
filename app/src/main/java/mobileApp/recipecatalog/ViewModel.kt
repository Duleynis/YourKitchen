package mobileApp.recipecatalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.Room.Entities.ReceiptTable

class ViewModel (private val receiptDAO: ReceiptDAO) : ViewModel() {

    val getAllReceipts = liveData {
        emit(receiptDAO.getAllReceipt())
    }

    fun insertReceipt(receipt : ReceiptTable){
        viewModelScope.launch {
            receiptDAO.insertReceipt(receipt)
        }
    }

    fun deleteReceipt(receipt : ReceiptTable){
        viewModelScope.launch {
            receiptDAO.removeReceipt(receipt)
        }
    }
}