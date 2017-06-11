package com.sxtanna.database.task

abstract class DatabaseTask<out R : AutoCloseable> {

	abstract val resource : R

}