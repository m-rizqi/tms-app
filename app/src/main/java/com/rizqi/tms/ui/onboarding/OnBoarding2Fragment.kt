package com.rizqi.tms.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentOnBoardingBinding

class OnBoarding2Fragment : Fragment() {
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
            title = getString(R.string.easy_cashier_feature)
            description = getString(R.string.scan_to_speed_up_merchant_performance)
            image = R.drawable.vector_onboarding2
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}