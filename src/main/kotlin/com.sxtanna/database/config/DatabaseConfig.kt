package com.sxtanna.database.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sxtanna.database.extensions.readJson
import com.sxtanna.database.extensions.writeJsonTo
import java.io.File

interface DatabaseConfig {


	companion object {

		val gson = createGson()


		inline fun <reified T : DatabaseConfig> loadOrSave(file : File, default: T): T {
			val loaded = loadFromFile(file, default)
			if (file.exists().not()) saveToFile(file, loaded!!)

			return loaded!!
		}

		@PublishedApi
		internal inline fun <reified T : DatabaseConfig> loadFromFile(file : File, default: T?): T? {
			if (file.exists().not()) return default

			val text = file.useLines { buildString { it.forEach { append(it) }} }
			return text.readJson<T>()
		}

		@PublishedApi
		internal inline fun <reified T : DatabaseConfig> saveToFile(file : File, type: T) {
			check(prepareWrite(file)) { "Failed to prepare file $file for writing" }

			type.writeJsonTo(file)
			//FileWriter(file).use { it.write(gson.toJson(type, T::class.java)) }
		}


		fun prepareWrite(file : File): Boolean {
			if (file.parentFile != null && file.parentFile.exists().not()) return file.parentFile.mkdirs()
			return true
		}

		fun createGson() : Gson {
			return GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create()
		}

	}

}