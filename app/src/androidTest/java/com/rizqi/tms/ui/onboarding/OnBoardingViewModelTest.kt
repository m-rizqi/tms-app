package com.rizqi.tms.ui.onboarding

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OnBoardingViewModelTest {

    private val viewModel = OnBoardingViewModel()

    @Test
    fun on_page_selected_state_updated() {
        assertThat(viewModel.uiState.value.fragmentIndex).isEqualTo(0)

        viewModel.onPageSelected(1)
        assertThat(viewModel.uiState.value.fragmentIndex).isEqualTo(1)

        viewModel.onPageSelected(1)
        assertThat(viewModel.uiState.value.fragmentIndex).isEqualTo(1)

    }
}