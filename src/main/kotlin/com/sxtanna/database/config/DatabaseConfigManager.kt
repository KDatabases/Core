package com.sxtanna.database.config

import com.sxtanna.database.base.Database
import java.io.File

interface DatabaseConfigManager<C : DatabaseConfig, out D : Database<*, C, *>> {

	operator fun get(config : C) : D

	operator fun get(file : File) = get(getConfig(file))

	fun getConfig(file : File) : C

}