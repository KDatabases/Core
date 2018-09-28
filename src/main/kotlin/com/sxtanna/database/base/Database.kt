package com.sxtanna.database.base

import com.sxtanna.database.config.DatabaseConfig
import com.sxtanna.database.task.DatabaseTask
import com.sxtanna.database.type.IDatabase
import com.sxtanna.database.type.Switch
import java.io.Closeable
import java.util.function.Consumer

/**
 * Base class that represents all database wrapper types
 *
 * @param [R] The resource type
 * @param [C] The config type
 * @param [T] The task type
 */
abstract class Database<R : Closeable, out C : DatabaseConfig, T : DatabaseTask<R>> : IDatabase<R, T>, Switch {

	abstract val conf : C
	abstract val name : String

	var isEnabled : Boolean = false
		private set


	/**
	 * Attempt to enable this Database.
	 *
	 * * Runs [load], then sets isEnabled to true
	 *
	 * @throws IllegalStateException if already enabled
	 */
	@Throws(IllegalStateException::class)
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
	@Throws(IllegalStateException::class)
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
	protected abstract fun load()

	/**
	 * Destroy the connection manager of this Database.
	 */
	protected abstract fun poison()


	/**
	 * Attempt to pull a resource from the connection manager.
	 *
	 * @return The [R] or null
	 */
	protected abstract fun poolResource() : R?

	/**
	 * Create a [T] for execution
	 *
	 * @return [T] ready for execution
	 */
	protected abstract fun createTask(resource : R) : T


	/**
	 * Attempt to pull a resource from the connection manager with checks.
	 *
	 * Checks
	 * * If the database is enabled first
	 * * If the resource is null before returning
	 *
	 * @throws IllegalStateException if any of the checks fail
	 */
	@Throws(IllegalStateException::class)
	fun resource() : R {
		check(isEnabled) { "Database $name is not enabled, resources cannot be pulled" }
		return checkNotNull(poolResource()) { "Failed to get resource from database $name pool" }
	}


	/**
	 * Invoke an action using this Database
	 *
	 * *Preferred function for Kotlin usage*
	 *
	 * @throws IllegalStateException if the resource can't be created
	 * @see [resource]
	 */
	@Throws(IllegalStateException::class)
	@JvmSynthetic // This hides the function from Java, do tell if this has a negative impact
	override operator fun invoke(block : T.() -> Unit) {
		resource().use { createTask(it).block() }
	}


	/**
	 * Invoke an action using this Database
	 *
	 * *Preferred function for Java usage*
	 *
	 * @throws IllegalStateException if the resource can't be created
	 * @see [resource]
	 */
	@Throws(IllegalStateException::class)
	override fun execute(block : Consumer<@ParameterName("task") T>) = invoke { block.accept(this) }

}