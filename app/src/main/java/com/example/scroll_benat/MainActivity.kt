package com.example.scroll_benat

import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * MainActivity es la actividad principal que administra la UI de la aplicación.
 * Permite añadir y eliminar tareas en una lista, guardándolas en SQLite.
 * Incluye efectos de sonido para interacciones exitosas y errores.
 */
class MainActivity : AppCompatActivity() {

    /** ImageView que muestra imagen cuando no hay tareas */
    private lateinit var imageViewNoTasks: ImageView
    /** Campo de texto donde el usuario escribe una nueva tarea. */
    private lateinit var etTask: EditText

    /** Botón para agregar una nueva tarea a la lista de tareas. */
    private lateinit var btnAddTask: Button

    /** RecyclerView que muestra la lista de tareas actuales. */
    private lateinit var rvTasks: RecyclerView

    /** Adaptador que gestiona la visualización de tareas en el RecyclerView. */
    private lateinit var adapter: TaskAdapter

    /** Objeto TaskBDHelper que gestiona la base de datos de tareas. */
    private lateinit var taskBDHelper: TaskBDHelper

    /** Lista mutable de tareas, que es la fuente de datos del RecyclerView. */
    private var tasks = mutableListOf<Task>()

    /** Sonido de confirmación para indicar una acción exitosa. */
    private lateinit var okSound: MediaPlayer

    /** Sonido de efectivo para indicar la eliminación exitosa de una tarea. */
    private lateinit var cashSound: MediaPlayer

    /** Sonido de error para indicar un problema o una acción incorrecta. */
    private lateinit var errorSound: MediaPlayer

    /**
     * Método onCreate que se llama al crear la actividad. Configura los elementos UI,
     * inicializa los sonidos y la base de datos.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el helper de la base de datos
        taskBDHelper = TaskBDHelper(this)

        // Inicializar sonidos
        okSound = MediaPlayer.create(this, R.raw.ok)
        cashSound = MediaPlayer.create(this, R.raw.cash)
        errorSound = MediaPlayer.create(this, R.raw.error)

        initUi()
    }

    /**
     * Método que inicializa la UI de la actividad. Configura las variables, listeners y el RecyclerView.
     */
    private fun initUi() {
        initVariables()
        initListeners()
        initRecyclerView()
        attachSwipeToDelete()
    }

    /**
     * Configura el RecyclerView, recupera las tareas guardadas en SQLite y las muestra en la lista.
     */
    private fun initRecyclerView() {
        tasks = taskBDHelper.getAllNotas().toMutableList() // Obtener tareas de SQLite
        rvTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasks) { deleteTask(it) }
        rvTasks.adapter = adapter
        updateNoTasksVisibility()
    }

    /**
     * Elimina una tarea de la lista en una posición específica, actualiza el adaptador y guarda los cambios.
     * Reproduce un sonido de eliminación al completar la acción.
     *
     * @param position Posición de la tarea a eliminar en la lista.
     */
    private fun deleteTask(position: Int) {
        val taskToDelete = tasks[position]
        taskBDHelper.deleteNota(taskToDelete.id) // Eliminar de SQLite
        tasks.removeAt(position)
        adapter.notifyDataSetChanged()
        cashSound.start()
        updateNoTasksVisibility()
    }

    /**
     * Inicializa las variables asociadas a los elementos de la UI.
     */
    private fun initVariables() {
        etTask = findViewById(R.id.etTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        rvTasks = findViewById(R.id.rvTasks)
        imageViewNoTasks = findViewById(R.id.imageViewNoTasks)
    }

    /**
     * Configura los listeners de la UI, como el botón para añadir tareas.
     */
    private fun initListeners() {
        btnAddTask.setOnClickListener { addTask() }
    }

    /**
     * Añade una nueva tarea a la lista si el campo de texto no está vacío.
     * Muestra un error y reproduce un sonido si el campo está vacío.
     * Si se añade exitosamente, limpia el campo de texto y reproduce un sonido de confirmación.
     */
    private fun addTask() {
        val newTaskTitle = etTask.text.toString()
        if (newTaskTitle.isEmpty()) {
            errorSound.start()
            etTask.error = "Escribe una tarea!"
        } else {
            // Crear un nuevo objeto Task y agregarlo a la base de datos
            val newTask = Task(0, newTaskTitle, "") // ID es 0 porque es auto-generado
            taskBDHelper.insertNota(newTask)
            tasks.add(newTask)
            adapter.notifyDataSetChanged()
            etTask.setText("")
            okSound.start()
            updateNoTasksVisibility()
        }
    }

    private fun attachSwipeToDelete() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // No se utiliza el movimiento vertical
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView // Obtiene la vista del ítem
                    itemView.setBackgroundColor(Color.RED) // Cambia el color del fondo del ítem a rojo
                    itemView.translationX = dX // Aplica la traducción en X para el desplazamiento
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition // Obtiene la posición del ítem deslizado
                deleteTask(position) // Llama a la función de eliminación
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT) // Restablece el color de fondo al original
            }
        }

        // Asocia el ItemTouchHelper al RecyclerView
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvTasks)
    }

    /**
     * Actualiza la visibilidad de la vista de la lista de tareas y de la imagen
     * que indica que no hay tareas disponibles.
     */
    private fun updateNoTasksVisibility() {
        if (tasks.isEmpty()) {
            rvTasks.visibility = View.GONE
            imageViewNoTasks.visibility = View.VISIBLE // Muestra la imagen
        } else {
            rvTasks.visibility = View.VISIBLE
            imageViewNoTasks.visibility = View.GONE // Oculta la imagen
        }
    }
}
