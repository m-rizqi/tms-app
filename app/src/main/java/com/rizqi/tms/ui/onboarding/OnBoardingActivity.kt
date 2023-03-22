package com.rizqi.tms.ui.onboarding

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityOnBoardingBinding
import com.rizqi.tms.ui.dp
import com.rizqi.tms.ui.getColorSafe
import com.rizqi.tms.ui.login.LoginActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOnBoardingBinding
    private val viewModel : OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {uiState ->

                }
            }
        }
        setupViewPagerAdapter()
        setupPageChangeCallback()
        binding.btnOnboardingNext.setOnClickListener {
            viewModel.nextPage()
            if (viewModel.uiState.value.shouldStartToLoginActivity) {
                startLoginActivity()
                return@setOnClickListener
            }
            binding.vpOnboarding.currentItem = viewModel.uiState.value.fragmentIndex
        }
        binding.tvOnboardingSkip.setOnClickListener {
            startLoginActivity()
        }
    }

    private fun setupPageChangeCallback() {
        binding.vpOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val fragmentIndex = viewModel.uiState.value.fragmentIndex
                    when(position){
                        0 -> {
                            if (fragmentIndex > position){
                                animateProgressIndicator(200, 100)
                            } else {
                                animateProgressIndicator(0, 100)
                            }
                            setupNextButton()
                        }
                        1 -> {
                            if (fragmentIndex > position){
                                animateProgressIndicator(300, 200)
                            } else {
                                animateProgressIndicator(100, 200)
                            }
                            setupNextButton()
                        }
                        2 -> {
                            animateProgressIndicator(200, 300)
                            setupStartButton()
                        }
                    }
                    viewModel.onPageSelected(position)
                }
            }
        )
    }

    private fun setupNextButton() {
        binding.btnOnboardingNext.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(resources.getColorSafe(R.color.primary_100))
            val paddingHorizontal = 12.dp(this@OnBoardingActivity)
            val paddingVertical = 8.dp(this@OnBoardingActivity)
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            cornerRadius = 4.dp(this@OnBoardingActivity)
            text = context.getString(R.string.next)
        }
    }

    private fun setupStartButton(){
        binding.btnOnboardingNext.apply {
            setBackgroundColor(resources.getColorSafe(R.color.primary_100))
            setTextColor(resources.getColorSafe(R.color.white))
            val paddingHorizontal = 20.dp(this@OnBoardingActivity)
            val paddingVertical = 16.dp(this@OnBoardingActivity)
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            cornerRadius = 50.dp(this@OnBoardingActivity)
            text = context.getString(R.string.start)
        }
    }

    private fun setupViewPagerAdapter(){
        val onBoardingContents = viewModel.uiState.value.onBoardingContents
        val viewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingContents)
        binding.vpOnboarding.adapter = viewPagerAdapter
    }

    private fun animateProgressIndicator(from : Int, to : Int){
        ValueAnimator.ofInt(from, to).apply {
            addUpdateListener {
                binding.lpiOnboarding.progress = it.animatedValue as Int
            }
            start()
        }
    }

    private fun startLoginActivity(){
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
        }
    }
}