import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mobileApp.recipecatalog.R

class StepsAdapter(
    private val steps: MutableList<String>
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepNumber : TextView = itemView.findViewById(R.id.stepNumber)
        val stepEditText: EditText = itemView.findViewById(R.id.stepEditText)

        val deleteStep_btn : ImageButton = itemView.findViewById(R.id.deleteStep_btn)

        // Храним текущий TextWatcher, чтобы удалять при повторном биндинге
        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]

        //Устанавливаем номер шага
        holder.stepNumber.text = holder.itemView.context
            .getString(R.string.stepNumber,
            position + 1)
        holder.stepNumber.setTextColor(Color.BLUE)

        // Убираем старый TextWatcher
        holder.textWatcher?.let { holder.stepEditText.removeTextChangedListener(it) }

        // Устанавливаем текст шага
        holder.stepEditText.setText(step)

        // Создаем новый TextWatcher
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    steps[pos] = s.toString()
                }
            }
        }

        holder.stepEditText.addTextChangedListener(watcher)
        holder.textWatcher = watcher

        holder.deleteStep_btn.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                steps.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, steps.size)
            }
        }
    }

    override fun getItemCount(): Int = steps.size

    // Добавление нового шага
    fun addStep(stepDescription : String) {
        steps.add(stepDescription)
        notifyItemInserted(steps.size - 1)
    }

    fun getSteps() : MutableList<String>{
        return steps
    }
}
