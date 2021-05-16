package com.snkrs.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.snkrs.BaseViewModel
import com.snkrs.MainActivity

abstract class BaseFragment<V : BaseViewModel>(
	private val modelClass: Class<V>,
	layoutId: Int
): Fragment(layoutId) {
	lateinit var viewModel: V

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = createViewModel()
	}

	private fun createViewModel(): V {
		return ViewModelProvider(
			this,
			(requireActivity() as MainActivity).graph.viewModelFactory
		).get(modelClass)
	}
}