import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import mobileApp.recipecatalog.R
import mobileApp.recipecatalog.Services.StepTimerService

class StepsAdapter(
    private val steps: MutableList<String>
) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    // Список времени каждого шага в секундах
    private val stepTimes = MutableList(steps.size) { 0 }

    // Таймеры для каждого шага по позиции
    private val timers = mutableMapOf<Int, CountDownTimer>()

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepNumber: TextView = itemView.findViewById(R.id.stepNumber)
        val stepEditText: EditText = itemView.findViewById(R.id.stepEditText)

        val hoursPicker: NumberPicker = itemView.findViewById(R.id.hoursPicker)
        val minutesPicker: NumberPicker = itemView.findViewById(R.id.minutesPicker)
        val secondsPicker: NumberPicker = itemView.findViewById(R.id.secondsPicker)
        val startTimerButton: Button = itemView.findViewById(R.id.startTimerButton)
        val deleteStep_btn: ImageButton = itemView.findViewById(R.id.deleteStep_btn)

        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]

        // Номер шага
        holder.stepNumber.text = holder.itemView.context.getString(R.string.stepNumber, position + 1)

        // Убираем старый TextWatcher
        holder.textWatcher?.let { holder.stepEditText.removeTextChangedListener(it) }

        // Текст шага
        holder.stepEditText.setText(step)

        // Настройка NumberPicker
        holder.hoursPicker.minValue = 0
        holder.hoursPicker.maxValue = 23
        holder.minutesPicker.minValue = 0
        holder.minutesPicker.maxValue = 59
        holder.secondsPicker.minValue = 0
        holder.secondsPicker.maxValue = 59

        // Синхронизация NumberPicker с stepTimes
        val totalSec = stepTimes.getOrElse(position) { 0 }
        holder.hoursPicker.value = totalSec / 3600
        holder.minutesPicker.value = (totalSec % 3600) / 60
        holder.secondsPicker.value = totalSec % 60

        // Слушатели изменений NumberPicker
        holder.hoursPicker.setOnValueChangedListener { _, _, newVal ->
            val sec = stepTimes.getOrElse(position) { 0 }
            val min = (sec % 3600) / 60
            val s = sec % 60
            stepTimes[position] = newVal * 3600 + min * 60 + s
        }
        holder.minutesPicker.setOnValueChangedListener { _, _, newVal ->
            val sec = stepTimes.getOrElse(position) { 0 }
            val h = sec / 3600
            val s = sec % 60
            stepTimes[position] = h * 3600 + newVal * 60 + s
        }
        holder.secondsPicker.setOnValueChangedListener { _, _, newVal ->
            val sec = stepTimes.getOrElse(position) { 0 }
            val h = sec / 3600
            val m = (sec % 3600) / 60
            stepTimes[position] = h * 3600 + m * 60 + newVal
        }

        // Новый TextWatcher для EditText
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

        holder.startTimerButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val durationSec = stepTimes[pos]
            if (durationSec <= 0) return@setOnClickListener

            // 1️⃣ Таймер для UI
            timers[pos]?.cancel() // отменяем старый таймер, если есть
            val uiTimer = object : CountDownTimer(durationSec * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secLeft = (millisUntilFinished / 1000).toInt()
                    holder.hoursPicker.value = secLeft / 3600
                    holder.minutesPicker.value = (secLeft % 3600) / 60
                    holder.secondsPicker.value = secLeft % 60
                }

                override fun onFinish() {
                    holder.hoursPicker.value = 0
                    holder.minutesPicker.value = 0
                    holder.secondsPicker.value = 0
                }
            }.start()
            timers[pos] = uiTimer

            // 2️⃣ Таймер в фоне + уведомление
            val context = holder.itemView.context
            val intent = Intent(context, StepTimerService::class.java).apply {
                putExtra(StepTimerService.EXTRA_STEP_NUMBER, pos + 1)
                putExtra(StepTimerService.EXTRA_DURATION, durationSec)
            }
            context.startForegroundService(intent)
        }

        // Удаление шага
        holder.deleteStep_btn.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                timers[pos]?.cancel()
                timers.remove(pos)

                steps.removeAt(pos)
                stepTimes.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, steps.size)
            }
        }
    }

    override fun getItemCount(): Int = steps.size

    fun addStep(stepDescription: String) {
        steps.add(stepDescription)
        stepTimes.add(0)
        notifyItemInserted(steps.size - 1)
    }

    fun updateData(newSteps: List<String>, durations: List<Int>) {
        steps.clear()
        steps.addAll(newSteps)
        stepTimes.clear()
        stepTimes.addAll(durations)
        notifyDataSetChanged()
    }

    fun getSteps(): MutableList<String> = steps

    fun getStepsWithTime(): List<StepWithTime> {
        return steps.mapIndexed { index, desc ->
            StepWithTime(desc, stepTimes.getOrElse(index) { 0 })
        }
    }

    fun cancelAllTimers() {
        timers.values.forEach { it.cancel() }
        timers.clear()
    }

    data class StepWithTime(
        val description: String,
        val durationSeconds: Int
    )
}
