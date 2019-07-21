/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.dynamicfeatureimplementation

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.domain.BaseModel
import com.example.domain.DataModel
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

const val DYNAMIC_FEATURE_MODULE = "dynamic_feature"

/**
 * The ViewModel for our single screen in the app.
 * It needs subclassing to override the initializeDynamicFeature() method
 */
abstract class AbstractMainViewModel(app: Application) : AndroidViewModel(app) {
    private val splitInstallManager = SplitInstallManagerFactory.create(app)
    private var sessionId = 0

   protected var dynamicFeature: DynamicFeature? = null

    private val listener = SplitInstallStateUpdatedListener { state ->
        if (state.sessionId() == sessionId) {
            when (state.status()) {
                SplitInstallSessionStatus.FAILED -> {
                    Log.d(TAG, "Module install failed with ${state.errorCode()}")
                    Toast.makeText(getApplication(), "Module install failed with ${state.errorCode()}", Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(getApplication(), "Storage module installed successfully", Toast.LENGTH_SHORT).show()
                }
                else -> Log.d(TAG, "Status: ${state.status()}")
            }
        }
    }

    init {
        splitInstallManager.registerListener(listener)
    }

    override fun onCleared() {
        splitInstallManager.unregisterListener(listener)
        super.onCleared()
    }


    fun getData(): BaseModel<DataModel>? {
        if (dynamicFeature == null) {
            if (isDynamicFeatureInstalled()) {
                initializeDynamicFeature()
            } else {
                requestDynamicFeatureInstall()
            }
        }


        return dynamicFeature?.getData()
    }

    protected abstract fun initializeDynamicFeature()

    private fun isDynamicFeatureInstalled() =
        if (BuildConfig.DEBUG) true else splitInstallManager.installedModules.contains(DYNAMIC_FEATURE_MODULE)

    private fun requestDynamicFeatureInstall() {
        Toast.makeText(getApplication(), "Requesting storage module install", Toast.LENGTH_SHORT).show()
        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule("dynamic_feature")
                .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { id -> sessionId = id }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error installing module: ", exception)
                Toast.makeText(getApplication(), "Error requesting module install", Toast.LENGTH_SHORT).show()
            }
    }



}