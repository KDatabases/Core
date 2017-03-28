package com.sxtanna.database.task

abstract class DatabaseTask<out R : AutoCloseable> {

	abstract val resource : R

	operator fun invoke(block: DatabaseTask<R>.() -> Unit) = this.block()

}