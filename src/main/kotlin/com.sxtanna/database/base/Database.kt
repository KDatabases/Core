package com.sxtanna.database.base

import com.sxtanna.database.config.DatabaseConfig
import com.sxtanna.database.task.DatabaseTask
import com.sxtanna.database.type.Switch

/**
 * Base class that represents all database wrapper types
 *
 * @param [R] The resource type
 * @param [C] The config type
 * @param [T] The task type
 */
abstract class Database<R : AutoCloseable, out C : DatabaseConfig, out T : DatabaseTask<R>> : Switch, (T.() -> Unit) -> Unit {

	abstract val name : String
	abstract protected val config : C

	var isEnabled : Boolean = false
		private set

	/**
	 * Attempt to enable this Database.
	 *
	 * * Runs [load], then sets isEnabled to true
	 *
	 * @throws IllegalStateException if already enabled
	 */
	override fun enable() {
		check(isEnabled.not()) { "Database $name is already enabled" }

		load()
		isEnabled = true
	}

	/**
	 * Attempt to disable this Database.
	 *
	 * * Runs [poison], then sets isEnabled to false
	 *
	 * @throws IllegalStateException if not enabled
	 */
	override fun disable() {
		check(isEnabled) { "Database $name isn't enabled" }

		try {
			poison()
		}
		catch (ex : Exception) {
			println("Failed to poison database $name")
		}
		isEnabled = false
	}


	/**
	 * Initialize the connection manager for this Database.
	 * If this throws an exception it will disrupt the enable process and cause calls to [resource] to throw exceptions
	 */
	abstract protected fun load()

	/**
	 * Destroy the connection manager of this Database.
	 */
	abstract protected fun poison()


	/**
	 * Attempt to pull a resource from the connection manager.
	 *
	 * @return The [R] or null
	 */
	abstract protected fun poolResource() : R?

	/**
	 * Create a [T] for execution
	 *
	 * @return [T] ready for execution
	 */
	abstract protected fun createTask(resource : R) : T


	fun resource() : R {
		check(isEnabled) { "Database $name is not enabled, resources cannot be pulled" }
		return checkNotNull(poolResource()) { "Failed to get resource from database $name pool" }
	}


	override fun invoke(block : T.() -> Unit) {
		resource().use { createTask(it).block() }
	}

}