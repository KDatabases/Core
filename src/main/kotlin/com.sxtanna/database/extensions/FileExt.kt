package com.sxtanna.database.extensions

import java.io.File
import java.io.FileWriter

/**
 * Created by camdenorrb on 3/28/17.
 */

inline fun File.write(write: (FileWriter) -> Unit) {
	if (this.parentFile.exists().not()) this.mkdirs()
	FileWriter(this).use(write)
}