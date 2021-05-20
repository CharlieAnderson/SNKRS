package com.snkrs.di

import android.content.Context
import com.snkrs.network.MainRepository
import com.snkrs.network.MainRemote

/**
 * dependency graph for the top-most dependencies, to inject throughout code.
 */
class DependencyGraph(applicationContext: Context) {
	val remote by lazy { MainRemote() }
	val repository by lazy { MainRepository(remote) }
	val viewModelFactory by lazy { ViewModelFactory(repository) }
}