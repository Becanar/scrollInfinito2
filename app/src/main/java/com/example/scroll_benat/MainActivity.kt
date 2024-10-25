package com.example.scroll_benat

import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView




class MainActivity : AppCompatActivity() {
    lateinit var etTask: EditText
    lateinit var btnAddTask: Button
    lateinit var rvTasks: RecyclerView

    lateinit var adapter:TaskAdapter
    lateinit var prefs: Preferences

    var tasks = mutableListOf<String>()

    private lateinit var okSound: MediaPlayer
    private lateinit var cashSound: MediaPlayer
    lateinit var errorSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Preferences(this)
        okSound = MediaPlayer.create(this, R.raw.ok)
        cashSound = MediaPlayer.create(this, R.raw.cash)
        errorSound = MediaPlayer.create(this, R.raw.error)
        initUi()
    }

    private fun initUi() {
        initVariables()
        initListeners()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        tasks = prefs.getTasks()
        rvTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasks) {deleteTask(it)}
        rvTasks.adapter = adapter

    }

    private fun deleteTask(position: Int) {
        tasks.removeAt(position)
        adapter.notifyDataSetChanged()
        prefs.saveTasks(tasks)
        cashSound.start()
    }


    private fun initVariables() {
        etTask = findViewById(R.id.etTask)
        btnAddTask = findViewById(R.id.btnAddTask)
        rvTasks = findViewById(R.id.rvTasks)
    }

    private fun initListeners() {
        btnAddTask.setOnClickListener {addTask()}
    }

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
}