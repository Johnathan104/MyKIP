package org.whynot.kipku.ui.viewModel
data class UiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String ="",
    val error: String? = null
)
