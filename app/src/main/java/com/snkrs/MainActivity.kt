package com.snkrs

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.snkrs.di.DependencyGraph

class MainActivity : FragmentActivity() {
	lateinit var graph: DependencyGraph

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_layout)
		graph = DependencyGraph(applicationContext)
	}
}