package com.sxtanna.database.extensions

import com.sxtanna.database.config.DatabaseConfig
import java.io.File

/**
 * Created by camdenorrb on 3/28/17.
 */

fun Any.toJson(): String = DatabaseConfig.gson.toJson(this)

fun Any.writeJsonTo(file: File) = file.write { it.write(toJson()) }


inline fun <reified T : Any> String.readJson(): T = DatabaseConfig.gson.fromJson(this, T::class.java)