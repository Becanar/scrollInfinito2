package com.example.scroll_benat

import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * MainActivity es la actividad principal que administra la UI de la aplicación.
 * Permite añadir y eliminar tareas en una lista, guardándolas en preferencias compartidas.
 * Incluye efectos de sonido para interacciones exitosas y errores.
 */
class MainActivity : AppCompatActivity() {

    /** Campo de texto donde el usuario escribe una nueva tarea. */
    lateinit var etTask: EditText

    /** Botón para agregar una nueva tarea a la lista de tareas. */
    lateinit var btnAddTask: Button

    /** RecyclerView que muestra la lista de tareas actuales. */
    lateinit var rvTasks: RecyclerView

    /** Adaptador que gestiona la visualización de tareas en el RecyclerView. */
    lateinit var adapter: TaskAdapter

    /** Objeto Preferences que guarda y recupera la lista de tareas del almacenamiento compartido. */
    lateinit var prefs: Preferences

    /** Lista mutable de tareas, que es la fuente de datos del RecyclerView. */
    var tasks = mutableListOf<String>()

    /** Sonido de confirmación para indicar una acción exitosa. */
    private lateinit var okSound: MediaPlayer

    /** Sonido de efectivo para indicar la eliminación exitosa de una tarea. */
    private lateinit var cashSound: MediaPlayer

    /** Sonido de error para indicar un problema o una acción incorrecta. */
    lateinit var errorSound: MediaPlayer

    /**
     * Método onCreate que se llama al crear la actividad. Configura los elementos UI,
     * inicializa los sonidos y las preferencias compartidas.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Preferences(this)
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
     * Configura el RecyclerView, recupera las tareas guardadas y las muestra en la lista.
     */
    private fun initRecyclerView() {
        tasks = prefs.getTasks()
        rvTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasks) { deleteTask(it) }
        rvTasks.adapter = adapter
    }

    /**
     * Elimina una tarea de la lista en una posición específica, actualiza el adaptador y guarda los cambios.
     * Reproduce un sonido de eliminación al completar la acción.
     *
     * @param position Posición de la tarea a eliminar en la lista.
     */
    private fun deleteTask(position: Int) {
        tasks.removeAt(position)
        adapter.notifyDataSetChanged()
        prefs.saveTasks(tasks)
        cashSound.start()
    }

    /**
     * Inicializa las variables asociadas a los elementos de la UI.
     */
    private fun initVariables() {
        etTask = findViewById(R.id.etTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        rvTasks = findViewById(R.id.rvTasks)
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
        val newTask = etTask.text.toString()
        if (newTask.isEmpty()) {
            errorSound.start()
            etTask.error = "Escribe una tarea!"
        } else {
            tasks.add(newTask)
            prefs.saveTasks(tasks)
            adapter.notifyDataSetChanged()
            etTask.setText("")
            okSound.start()
        }
    }
    private fun attachSwipeToDelete() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            /**
             * Método que maneja el movimiento de un elemento dentro del RecyclerView.
             * En este caso, no se utiliza, así que devuelve false.
             *
             * @param recyclerView El RecyclerView donde ocurre el movimiento.
             * @param viewHolder El ViewHolder del elemento que se está moviendo.
             * @param target El ViewHolder del elemento objetivo donde se está moviendo.
             * @return false porque no se usa el movimiento.
             */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No se utiliza el movimiento vertical
            }

            /**
             * Método que se llama para dibujar el ítem mientras se desliza.
             * Cambia el color del fondo del ítem a rojo mientras se desliza.
             *
             * @param c Canvas donde se dibuja el RecyclerView.
             * @param recyclerView El RecyclerView.
             * @param viewHolder El ViewHolder del ítem que se desliza.
             * @param dX Desplazamiento en X durante el deslizamiento.
             * @param dY Desplazamiento en Y durante el deslizamiento.
             * @param actionState Estado actual del gesto (deslizar, arrastrar, etc.).
             * @param isCurrentlyActive Indica si el gesto está activo.
             */
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView // Obtiene la vista del ítem

                    // Cambia el color del fondo del ítem a rojo mientras se desliza
                    itemView.setBackgroundColor(Color.RED)

                    // Aplica la traducción en X para el desplazamiento
                    itemView.translationX = dX
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            /**
             * Método que se llama cuando un ítem ha sido deslizado.
             * Llama a la función de eliminación después del deslizamiento.
             *
             * @param viewHolder El ViewHolder que fue deslizado.
             * @param direction La dirección en la que se deslizó (izquierda o derecha).
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition // Obtiene la posición del ítem deslizado
                deleteTask(position) // Llama a la función de eliminación
            }

            /**
             * Método que se llama para limpiar la vista del ítem después de que se ha deslizado.
             * Restablece el color de fondo al original.
             *
             * @param recyclerView El RecyclerView donde se encuentra el ítem.
             * @param viewHolder El ViewHolder del ítem que fue deslizado.
             */
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Restablece el color de fondo al original después de que el usuario deja de deslizar
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // Asocia el ItemTouchHelper al RecyclerView
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvTasks)
    }
}
