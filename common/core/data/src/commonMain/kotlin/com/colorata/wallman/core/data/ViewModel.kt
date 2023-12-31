package com.colorata.wallman.core.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


@PublishedApi
@Suppress("UNCHECKED_CAST")
internal class ViewModelFactory(val viewModel: () -> ViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel() as T
    }
}

@Composable
inline fun <reified T : ViewModel> viewModel(noinline block: @DisallowComposableCalls () -> T): T {
    return androidx.lifecycle.viewmodel.compose.viewModel(factory = remember {
        ViewModelFactory {
            block()
        }
    })
}