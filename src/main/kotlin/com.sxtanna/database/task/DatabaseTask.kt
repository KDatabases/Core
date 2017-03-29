package com.sxtanna.database.task

abstract class DatabaseTask<out R : AutoCloseable> {

	abstract val resource : R

	// Added Inline - Mr.Midnight
	operator inline fun invoke(block: DatabaseTask<R>.() -> Unit) = this.block()

}