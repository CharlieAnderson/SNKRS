package com.snkrs

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher

abstract class BaseViewModel(dispatcher: CoroutineDispatcher): ViewModel()