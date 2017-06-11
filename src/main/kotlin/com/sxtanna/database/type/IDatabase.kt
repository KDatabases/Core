package com.sxtanna.database.type

import com.sxtanna.database.task.DatabaseTask
import java.util.function.Consumer

interface IDatabase<out R : AutoCloseable, T : DatabaseTask<R>> {

	@JvmSynthetic
	operator fun invoke(block : T.() -> Unit)

	fun execute(block : Consumer<T>)

}