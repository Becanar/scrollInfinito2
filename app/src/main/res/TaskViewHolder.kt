import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewHolder para mostrar y gestionar la vista de una tarea individual en un RecyclerView.
 * 
 * @param view Vista que representa un elemento de tarea en la lista.
 */
class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    /** TextView para mostrar el texto de la tarea. */
    private val tvTask: TextView = view.findViewById(R.id.tvTask)

    /** ImageView que representa una acción para marcar la tarea como realizada. */
    private val ivTaskDone: ImageView = view.findViewById(R.id.ivTaskDone)

    /**
     * Método para renderizar la tarea en la vista y asignar un listener para marcar la tarea como completada.
     *
     * @param task Texto de la tarea a mostrar.
     * @param onItemDone Función de callback que se llama cuando se marca la tarea como realizada.
     */
    fun render(task: String, onItemDone: (Int) -> Unit) {
        tvTask.text = task
        ivTaskDone.setOnClickListener { onItemDone(adapterPosition) }
    }
}
