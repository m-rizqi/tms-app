package com.rizqi.tms.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentOnBoardingBinding

class OnBoardingFragment(
    private val onBoardingContent: OnBoardingViewModel.OnBoardingContent
) : Fragment(){

    private var _binding : FragmentOnBoardingBinding? = null
    private val binding : FragmentOnBoardingBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            title = getString(onBoardingContent.titleResId)
            description = getString(onBoardingContent.descriptionResId)
            imageResId = onBoardingContent.imageResId
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}