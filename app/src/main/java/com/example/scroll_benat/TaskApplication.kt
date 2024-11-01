package com.example.scroll_benat

import android.app.Application

/**
 * Clase TaskApplication que extiende Application.
 * Se utiliza para inicializar las preferencias compartidas en toda la aplicación.
 */
class TaskApplication : Application() {

    companion object {
        /** Instancia de Preferences accesible globalmente en la aplicación. */
        lateinit var prefs: Preferences
    }

    /**
     * Método onCreate que se llama al iniciar la aplicación.
     * Inicializa la instancia de Preferences con el contexto base de la aplicación.
     */
    override fun onCreate() {
        super.onCreate()
        prefs = Preferences(baseContext)
    }
}
