package com.sxtanna.database.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sxtanna.database.config.DatabaseConfig
import java.io.File


val gson : Gson by lazy { GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create() }


inline fun <reified T : DatabaseConfig> File.loadOrSave(default: T): T {
	val loaded = this.loadFromFile(default)
	if (this.exists().not()) this.saveToFile(loaded!!)

	return loaded!!
}

@PublishedApi
internal inline fun <reified T : DatabaseConfig> File.loadFromFile(default: T?): T? {
	if (this.exists().not()) return default

	val text = this.useLines { buildString { it.forEach { append(it) }} }
	return gson.fromJson(text, T::class.java)
}

@PublishedApi
internal inline fun <reified T : DatabaseConfig> File.saveToFile(type: T) {
	check(prepareWrite(this)) { "Failed to prepare file $this for writing" }

	this.writeText(gson.toJson(type, T::class.java))
}


fun prepareWrite(file : File): Boolean {
	if (file.parentFile != null && file.parentFile.exists().not()) return file.parentFile.mkdirs()
	return true
}