package com.example.scroll_benat

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase Preferences para gestionar el almacenamiento y recuperación de las tareas en
 * preferencias compartidas. Guarda la lista de tareas como un conjunto de cadenas.
 *
 * @param context Contexto de la aplicación necesario para acceder a las preferencias compartidas.
 */
class Preferences(context: Context) {

    companion object {
        /** Nombre del archivo de preferencias compartidas. */
        const val PREFS_NAME = "myDatabase"

        /** Clave para almacenar y recuperar la lista de tareas. */
        const val TASKS = "tasks_value"
    }

    /** Objeto SharedPreferences para el acceso a las preferencias compartidas. */
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    /**
     * Guarda una lista de tareas en las preferencias compartidas.
     *
     * @param tasks Lista de tareas a guardar.
     */
    fun saveTasks(tasks: List<String>) {
        prefs.edit().putStringSet(TASKS, tasks.toSet()).apply()
    }

    /**
     * Recupera la lista de tareas almacenadas en las preferencias compartidas.
     *
     * @return Lista mutable de tareas recuperadas. Si no hay tareas almacenadas,
     *         devuelve una lista vacía.
     */
    fun getTasks(): MutableList<String> {
        return prefs.getStringSet(TASKS, emptySet<String>())?.toMutableList() ?: mutableListOf()
    }
}
