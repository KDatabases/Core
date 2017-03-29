package com.sxtanna.database.config

import com.sxtanna.database.base.Database
import com.sxtanna.database.config.DatabaseConfig
import java.io.File

interface DatabaseConfigManager<out C : DatabaseConfig, out D : Database<*, C, *>> {

	operator fun get(file : File) : D

	fun getConfig(file : File) : C

}