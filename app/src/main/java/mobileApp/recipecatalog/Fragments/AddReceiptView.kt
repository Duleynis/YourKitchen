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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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
    private val viewModel : RecipeViewModel by activityViewModel()
    private lateinit var stepsAdapter: StepsAdapter
    private lateinit var root : View
    private lateinit var receiptTitle : EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var receiptImage: ImageView
    private lateinit var ingredientsDescription: EditText
    private lateinit var stepRecyclerView: RecyclerView
    private lateinit var addStep_btn : Button
    private lateinit var saveReceipt_btn : Button
    private var stepsList = mutableListOf<String>()

    private lateinit var spinnerAdapter : ArrayAdapter<String>
    private var selectedImageURI : Uri? = null
    private var image_path : String? = null
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageURI = it
            receiptImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_add_receipt__view, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()

        val receiptID = arguments?.getInt("receiptID", -1) ?: -1

        if(receiptID != - 1){
            viewModel.getReceiptWithSteps(receiptID).observe(viewLifecycleOwner) { (receipt, steps) ->
                receiptTitle.setText(receipt.title)

                val position = spinnerAdapter.getPosition(receipt.category)
                categorySpinner.setSelection(position)

                val file = File(receipt.imagePath)
                if(file.exists()){
                    image_path = receipt.imagePath
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    receiptImage.setImageBitmap(bitmap)
                }

                ingredientsDescription.setText(receipt.ingredientsDescription)

                steps.forEach {
                    stepsList.add(it.stepDescription)
                }
                stepsAdapter = StepsAdapter(stepsList)
                stepRecyclerView.adapter = stepsAdapter
                stepRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            }
        }

        else{
            stepsAdapter = StepsAdapter(stepsList)
            stepRecyclerView.adapter = stepsAdapter
            stepRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        }

        receiptImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        addStep_btn.setOnClickListener {
            stepsAdapter.addStep("")
            stepRecyclerView.scrollToPosition(stepsAdapter.itemCount - 1)
        }

        saveReceipt_btn.setOnClickListener {
            val title = receiptTitle.text.toString()
            val spinnerPosition = categorySpinner.selectedItemPosition
            val ingredientsDesc = ingredientsDescription.text.toString()

            if(title.isEmpty() || spinnerPosition == 0 || ingredientsDesc.isEmpty()){
                Snackbar.make(requireView(), "Заполните название, категорию рецепта, а также поле ингредиенты!",
                    Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dir = File(requireContext().filesDir, "images")
            if(!dir.exists()) dir.mkdirs()

            val receipt: ReceiptTable
            if(selectedImageURI != null){
                val inputStream = requireContext().contentResolver.openInputStream(selectedImageURI!!)
                val file = File(dir, "$title.jpg")
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                receipt = ReceiptTable(
                    title = title,
                    category = categorySpinner.selectedItem.toString(),
                    ingredientsDescription = ingredientsDesc ,
                    imagePath = file.absolutePath
                )
            }
            else if (receiptImage.drawable == null){
                receipt = ReceiptTable(
                    title = title,
                    category = categorySpinner.selectedItem.toString(),
                    ingredientsDescription = ingredientsDesc,
                    imagePath = "noSelected"
                )
            }
            else{
                receipt = ReceiptTable(
                    title = title,
                    category = categorySpinner.selectedItem.toString(),
                    ingredientsDescription = ingredientsDesc,
                    imagePath = image_path!!
                )
            }

            val stepsDescription = stepsAdapter.getSteps()
            if(stepsDescription.isNotEmpty()){
                viewModel.insertReceipt(receipt){ receiptID ->
                    val steps : MutableList<StepReceiptTable> = mutableListOf()
                    for (index in stepsDescription.indices){
                        if(stepsDescription[index].isNotEmpty()){
                            val step = StepReceiptTable(
                                recipeID = receiptID,
                                stepNumber = index + 1,
                                stepDescription = stepsDescription[index]
                            )
                            steps.add(step)
                        }
                        else{
                            Snackbar.make(requireView(), "Заполните все шаги приготовления или удалите лишние!",
                                Snackbar.LENGTH_LONG).show()
                            return@insertReceipt
                        }
                    }
                    viewModel.insertReceiptSteps(steps)
                    findNavController().popBackStack()
                }
            }
            else
                Snackbar.make(requireView(), "В рецепте должен быть по крайней мере один шаг приготовления!",
                    Snackbar.LENGTH_LONG).show()
        }
    }

    fun SetSpinner() {
        val categories = listOf(
            "Выберите категорию рецепта",
            "Салаты", "Первые блюда", "Вторые блюда", "Гарниры", "Десерты", "Напитки"
        )

        spinnerAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).apply {
                    textSize = 16f
                    setTextColor(Color.BLACK)
                }
                return view
            }
        }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter
    }

    fun initializeComponents(){
        receiptTitle = root.findViewById(R.id.receiptTitle)

        categorySpinner = root.findViewById(R.id.categoryReceiptSpinner)
        SetSpinner()

        ingredientsDescription = root.findViewById(R.id.ingredientsEditText)
        receiptImage = root.findViewById(R.id.receiptImage)

        stepRecyclerView = root.findViewById(R.id.stepRecyclerView)
        addStep_btn = root.findViewById(R.id.add_step_btn)
        saveReceipt_btn = root.findViewById(R.id.save_receipt_btn)
    }
}