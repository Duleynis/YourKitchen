package mobileApp.recipecatalog.Configuration

import androidx.room.Room
import mobileApp.recipecatalog.Room.AppDataBase
import mobileApp.recipecatalog.Room.DAO.ReceiptDAO
import mobileApp.recipecatalog.RecipeViewModel
import mobileApp.recipecatalog.Room.DAO.StepDAO
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


//Задаем конфигурацию для ViewModule и всех Module
val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            "myDB.db"
        ).build()
    }

    single<ReceiptDAO> { get<AppDataBase>().receiptDAO() }
    single<StepDAO> { get<AppDataBase>().stepDAO() }
    viewModel { RecipeViewModel(get(), get()) }
}