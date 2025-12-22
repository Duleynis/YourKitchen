package mobileApp.recipecatalog.Fragments

import StepsAdapter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import mobileApp.recipecatalog.R
import mobileApp.recipecatalog.RecipeViewModel
import mobileApp.recipecatalog.Room.Entities.ReceiptTable
import mobileApp.recipecatalog.Room.Entities.StepReceiptTable
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class AddReceiptView : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModel()

    private lateinit var root: View
    private lateinit var receiptTitle: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var receiptImage: ImageView
    private lateinit var ingredientsDescription: EditText
    private lateinit var stepRecyclerView: RecyclerView
    private lateinit var addStepBtn: Button
    private lateinit var saveReceiptBtn: Button
    private lateinit var stepsAdapter: StepsAdapter

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private var imageUri: Uri? = null
    private var imagePath: String? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                receiptImage.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_add_receipt__view, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initRecycler()

        val receiptId = arguments?.getInt("receiptID", -1) ?: -1
        if (receiptId != -1) loadReceipt(receiptId)

        receiptImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        addStepBtn.setOnClickListener {
            stepsAdapter.addStep("")
            stepRecyclerView.scrollToPosition(stepsAdapter.itemCount - 1)
        }

        saveReceiptBtn.setOnClickListener {
            saveReceipt(receiptId)
        }
    }

    // -------------------- LOAD --------------------

    private fun loadReceipt(id: Int) {
        viewModel.getReceiptWithSteps(id).observe(viewLifecycleOwner) { (receipt, steps) ->

            receiptTitle.setText(receipt.title)
            ingredientsDescription.setText(receipt.ingredientsDescription)

            categorySpinner.setSelection(
                spinnerAdapter.getPosition(receipt.category)
            )

            val file = File(receipt.imagePath)
            if (file.exists()) {
                imagePath = receipt.imagePath
                receiptImage.setImageBitmap(
                    BitmapFactory.decodeFile(file.absolutePath)
                )
            }

            // Получаем текст и время шагов
            val stepTexts = steps.map { it.stepDescription }
            val stepDurations = steps.map { it.durationSeconds }

            // Обновляем адаптер с текстом и временем
            stepsAdapter.updateData(stepTexts, stepDurations)
        }
    }

    // -------------------- SAVE --------------------

    private fun saveReceipt(receiptId: Int) {

        val title = receiptTitle.text.toString()
        val ingredients = ingredientsDescription.text.toString()
        val categoryPos = categorySpinner.selectedItemPosition

        if (title.isBlank() || ingredients.isBlank() || categoryPos == 0) {
            Snackbar.make(root, "Заполните все поля", Snackbar.LENGTH_LONG).show()
            return
        }

        val image = saveImage(title)

        val receipt = ReceiptTable(
            foodID = if (receiptId == -1) 0 else receiptId,
            title = title,
            category = categorySpinner.selectedItem.toString(),
            ingredientsDescription = ingredients,
            imagePath = image
        )

        val stepsText = stepsAdapter.getSteps()
        if (stepsText.any { it.isBlank() }) {
            Snackbar.make(root, "Заполните все шаги", Snackbar.LENGTH_LONG).show()
            return
        }

        if (receiptId == -1) {
            // ➕ Новый рецепт
            viewModel.insertReceipt(receipt) { newId ->
                saveSteps(newId)
                findNavController().popBackStack()
            }
        } else {
            // ✏ Обновление
            viewModel.updateReceipt(receipt)
            viewModel.deleteStepsByRecipeId(receiptId.toLong())
            saveSteps(receiptId.toLong())
            findNavController().popBackStack()
        }
    }

    private fun saveSteps(recipeId: Long) {
        val stepsWithTime = stepsAdapter.getStepsWithTime()
        val entities = stepsWithTime.mapIndexed { index, step ->
            StepReceiptTable(
                recipeID = recipeId,
                stepNumber = index + 1,
                stepDescription = step.description,
                durationSeconds = step.durationSeconds // добавляем поле в сущность БД
            )
        }
        viewModel.insertReceiptSteps(entities)
    }

    private fun saveImage(title: String): String {
        val dir = File(requireContext().filesDir, "images")
        if (!dir.exists()) dir.mkdirs()

        return when {
            imageUri != null -> {
                val file = File(dir, "$title.jpg")
                requireContext().contentResolver.openInputStream(imageUri!!).use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                file.absolutePath
            }

            imagePath != null -> imagePath!!

            else -> "noImage"
        }
    }

    // -------------------- INIT --------------------

    private fun initViews() {
        receiptTitle = root.findViewById(R.id.receiptTitle)
        categorySpinner = root.findViewById(R.id.categoryReceiptSpinner)
        ingredientsDescription = root.findViewById(R.id.ingredientsEditText)
        receiptImage = root.findViewById(R.id.receiptImage)
        stepRecyclerView = root.findViewById(R.id.stepRecyclerView)
        addStepBtn = root.findViewById(R.id.add_step_btn)
        saveReceiptBtn = root.findViewById(R.id.save_receipt_btn)

        setupSpinner()
    }

    private fun initRecycler() {
        stepsAdapter = StepsAdapter(mutableListOf())
        stepRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        stepRecyclerView.adapter = stepsAdapter
    }

    private fun setupSpinner() {
        val categories = listOf(
            "Выберите категорию рецепта",
            "Салаты", "Первые блюда", "Вторые блюда",
            "Гарниры", "Десерты", "Напитки"
        )

        spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    (this as TextView).setTextColor(Color.BLACK)
                }
            }
        }

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        categorySpinner.adapter = spinnerAdapter
    }
}
