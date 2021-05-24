package com.snkrs.di

import android.content.Context
import com.snkrs.network.MainRepository
import com.snkrs.network.MainRemote
import kotlinx.coroutines.Dispatchers


/**
 * dependency graph for the top-most dependencies, to inject throughout code.
 */
class DependencyGraph(applicationContext: Context) {
	private val dispatcher = Dispatchers.IO

	private val remote by lazy { MainRemote() }
	private val repository by lazy { MainRepository(remote) }
	val viewModelFactory by lazy { ViewModelFactory(dispatcher, repository) }
}