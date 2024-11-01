package com.example.scroll_benat


/**
 * como va a ser una clase para guardar datos la precedemos por data,
 * @param id Identificador
 * @param titulo el titulo de la nota
 * @param descripcion la descripcion mas o menos larga de la nota
 */
data class Task (val id : Int, val titulo : String, val descripcion:String){
    /**
    * Método para obtener el título de la tarea.
    *
    * @return El título de la tarea.
    */
    fun getTitle(): String {
        return titulo
    }
}