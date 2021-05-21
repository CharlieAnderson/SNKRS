package com.snkrs.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.snkrs.BaseViewModel
import com.snkrs.MainActivity
import com.snkrs.databinding.ActivityMainLayoutBinding
import com.snkrs.databinding.CarouselCenterItemLayoutBinding
import com.snkrs.databinding.FragmentCarouselLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseFragment<ViewModel : BaseViewModel, Binding : ViewBinding>: Fragment() {
	lateinit var viewModel: ViewModel
	private val job = Job()
	private val uiScope = CoroutineScope(Dispatchers.Main + job)
	private var _binding: Binding? = null
	// scoped between onCreateView and onDestroy
	protected val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = getViewBinding()
		setupViews()
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = createViewModel()
		setupListeners()
		observeData()
	}

	fun createViewModel(): ViewModel {
		return ViewModelProvider(
			this,
			(requireActivity() as MainActivity).graph.viewModelFactory
		).get(getViewModelClass())
	}

	override fun onDestroyView() {
		super.onDestroyView()
		job.cancel()
		_binding = null
	}

	// No-Op, optionally overridden
	open fun observeData() {}

	// No-Op, optionally overridden
	open fun setupListeners() {}

	// No-Op, optionally overridden
	open fun setupViews() {}

	abstract fun getViewModelClass(): Class<ViewModel>

	abstract fun getViewBinding(): Binding
}