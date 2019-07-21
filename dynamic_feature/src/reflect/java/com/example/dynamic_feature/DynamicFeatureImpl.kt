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
package com.example.dynamic_feature

import android.content.Context
import com.example.domain.BaseModel
import com.example.domain.DataModel
import com.example.dynamicfeatureimplementation.DynamicFeature
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.Charset


class DynamicFeatureImpl(val context: Context) : DynamicFeature {
    private val gson = lazy { Gson() }


    /**
     * get data from assets parse it and create and return model
     */
    override fun getData(): BaseModel<DataModel> {
        val json = loadJSONFromAsset("data.json")
        val typeToken = object : TypeToken<BaseModel<DataModel>>() {}.type
        return gson.value.fromJson<BaseModel<DataModel>>(json, typeToken)
    }

    /**
     * The provider singleton. It is accessed from the base app ViewModel through reflection.
     */
    companion object Provider : DynamicFeature.Provider {
        override fun get(dependencies: DynamicFeature.Dependencies): DynamicFeature {
            return DynamicFeatureImpl(dependencies.getContext())
        }
    }

    /**
     *  load data from assets
     */
    private fun loadJSONFromAsset(fileName: String): String? {
        val json: String
        try {
            val `is` = context.assets.open(fileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = buffer.toString(Charset.forName("utf8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}
