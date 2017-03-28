package com.sxtanna.database

import com.sxtanna.database.config.DatabaseConfig
import com.sxtanna.database.task.DatabaseTask

abstract class Database<R : AutoCloseable, out C : DatabaseConfig> : (DatabaseTask<R>.() -> Unit) -> Unit {

	abstract val name : String
	abstract protected val config : C

	var isEnabled : Boolean = false
		private set


	fun enable() {
		check(isEnabled.not()) { "Database $name is already enabled" }

		load()
		isEnabled = true
	}

	fun disable() {
		check(isEnabled) { "Database $name isn't enabled" }

		poison()
		isEnabled = false
	}


	abstract protected fun load()

	abstract protected fun poison()


	abstract protected fun poolResource() : R?

	abstract protected fun createTask(resource : R) : DatabaseTask<R>


	fun resource() : R = checkNotNull(poolResource()) { "Failed to get resource from database $name pool" }


	override operator fun invoke(block : DatabaseTask<R>.() -> Unit) {
		resource().use { createTask(it).block() }
	}

}