package com.snkrs.di

import android.content.Context
import com.snkrs.MainRepository
import com.snkrs.MainRemote

class DependencyGraph(applicationContext: Context) {
	val remote by lazy { MainRemote() }
	val repository by lazy { MainRepository(remote) }
	val viewModelFactory by lazy { ViewModelFactory(repository) }
}