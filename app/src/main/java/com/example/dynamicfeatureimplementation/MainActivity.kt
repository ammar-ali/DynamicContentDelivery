package com.example.dynamicfeatureimplementation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.google.android.samples.dynamiccodeloading.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val data = viewModel.getData()
        data?.let {
            Log.e("Title: ", data.body.title)
            Log.e("Message: ", data.body.message)
        }

    }
}
