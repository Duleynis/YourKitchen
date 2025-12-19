package mobileApp.recipecatalog

import mobileApp.recipecatalog.Room.Entities.ReceiptTable

interface IReceiptDelete {
    fun deleteReceipt(receiptTitle: String)
}