package com.example.scroll_benat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Clase de gestión de la base de datos
 */

class TaskBDHelper (context: Context) : SQLiteOpenHelper(
        context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val   DATABASE_NAME = "notas.db"
        private const val   DATABASE_VERSION = 1
        private const val   TABLE_NAME = "notas"
        // y ahora las columnas o atributos
        private const val   COLUMN_ID = "id"
        private const val   COLUMN_TITLE = "titulo"
        private const val   COLUMN_DESCRIPCION = "descripcion"

    }

    /**
     * Creadora de la tabla
     */
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY, " +
                    "$COLUMN_TITLE TEXT, " +
                    "$COLUMN_DESCRIPCION TEXT)"
        db?.execSQL(createTableQuery)
    }

    /**
     * Modifica la tabla de la base de datos
     */

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery =
            "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        // ya hemos mirado si existia o no, ahora la podemos crear sin que de errores
        onCreate(db)
    }

    /**
     * Pasado un objeto nota, la añade a la base de datos
     * @param nota la nota a añadir
     */

    fun insertNota(nota: Task){
        //la abro en modo escritura
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE,       nota.titulo)
            put(COLUMN_DESCRIPCION, nota.descripcion)
            //en este caso ID es autonumérico
        }
        //insertamos datos
        db.insert(TABLE_NAME, null, values)
        //y ahora a cerrar
        db.close()
    }

    /**
     * Lee la base de datos y rellena una List de tipo Nota
     */

    fun getAllNotas() : List<Task> {
        //creo una lista mutable para poder cambiar cosas
        val listaNotas = mutableListOf<Task>()

        //la abro en modo lectura
        val db = readableDatabase

        val query = "SELECT * FROM $TABLE_NAME"

        //lanza un cursor
        val cursor = db.rawQuery(query, null)

        //itera mientras que exista otro
        while (cursor.moveToNext()){
            val id =            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val titulo =        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val descripcion =   cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION))
            //creamos un objeto temporal de tipo Nota
            val nota =  Task(id, titulo, descripcion)
            //añadimos la nota
            listaNotas.add(nota)
        }
        //cerrar las conexiones
        cursor.close()
        db.close()

        return listaNotas
    }



    /**
     * Recibe un integer que es su posición en la lista, con esto recuperara los datos de la nota
     * @return Una nota en función del integer que es su posicion
     * @param idNota el número de la posición de la nota
     */

    fun getIdNota(idNota : Int) : Task {
        val db = readableDatabase

        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=$idNota"

        //lanza un cursor
        val cursor = db.rawQuery(query, null)
        //ve al primer registro que cumpla esa condicion (esperemos que el único)
        cursor.moveToFirst()

        //leo los datos de la consulta
        val id =            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val titulo =        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val descripcion =   cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION))

        //cierro conexiones y devuelvo la nota
        cursor.close()
        db.close()
        return Task(id, titulo, descripcion)
    }


    /**
     * Le pasamos una nota y actualiza su contenido, tiene una sintaxis especial, no lo hace con un update de sql normal
     */

    fun updateNota(nota: Task) {
        val db = writableDatabase
        // creamos los valores a cambiar
        val values = ContentValues().apply {
            put(COLUMN_TITLE, nota.titulo)
            put(COLUMN_DESCRIPCION, nota.descripcion)
        }

        // parametro con las condiciones a cumplir
        val whereClausula   = "$COLUMN_ID = ?"
        //prametro con la lista de elmentos a buscar
        val whereArgs       = arrayOf(nota.id.toString())

        //ejecución den esta tabla con esos valores en esa clausula con esas condiciones
        db.update(TABLE_NAME, values, whereClausula, whereArgs)

        db.close()
    }


    /**
     * Elimina una nota dada su id
     */
    fun deleteNota (idNota : Int) {
        val db = writableDatabase

        val whereClauses    = "$COLUMN_ID = ?"
        val whereArgs       = arrayOf(idNota.toString())
        //eliminar el objeto
        db.delete(TABLE_NAME,whereClauses,whereArgs)

        db.close()
    }

}