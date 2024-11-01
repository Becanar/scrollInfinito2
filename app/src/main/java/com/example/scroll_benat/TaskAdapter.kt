package com.example.scroll_benat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Adaptador para gestionar y mostrar una lista de tareas en un RecyclerView.
 *
 * @param tasks Lista de tareas a mostrar.
 * @param onItemDone Función de callback que se llama cuando un elemento de la lista se marca como completado.
 */
class TaskAdapter(private val tasks: List<String>, private val onItemDone: (Int) -> Unit) : RecyclerView.Adapter<TaskViewHolder>() {

    /**
     * Crea y devuelve un ViewHolder para una tarea.
     *
     * @param parent El ViewGroup al que se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder.
     * @return Un nuevo TaskViewHolder inicializado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.item_task, parent, false))
    }

    /**
     * Devuelve el número de elementos en la lista de tareas.
     *
     * @return Número total de tareas.
     */
    override fun getItemCount() = tasks.size

    /**
     * Vincula los datos de una tarea específica al ViewHolder correspondiente.
     *
     * @param holder ViewHolder que se actualizará con los datos de la tarea.
     * @param position Posición de la tarea en la lista.
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.render(tasks[position], onItemDone)
    }
}
