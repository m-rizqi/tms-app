package com.rizqi.tms.ui.onboarding

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityOnBoardingBinding
import com.rizqi.tms.ui.login.LoginActivity
import com.rizqi.tms.utility.dp
import com.rizqi.tms.utility.getColorSafe

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOnBoardingBinding
    private var lastPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = OnBoardingViewPagerAdapter(this)
        binding.vpOnboarding.adapter = pagerAdapter
        binding.vpOnboarding.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback(){
                @SuppressLint("Recycle")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when(position){
                        0 -> {
                            if (lastPosition > position){
                                animateProgressIndicator(200, 100)
                            }else{
                                animateProgressIndicator(0, 100)
                            }
                            lastPosition = position
                            setNextButton()
                        }
                        1 -> {
                            if (lastPosition > position){
                                animateProgressIndicator(300, 200)
                            }else{
                                animateProgressIndicator(100, 200)
                            }
                            lastPosition = position
                            setNextButton()
                        }
                        2 -> {
                            animateProgressIndicator(200, 300)
                            lastPosition = position
                            setStartButton()
                        }
                    }
                }
            }
        )
        binding.btnOnboardingNext.setOnClickListener {
            binding.vpOnboarding.apply {
                if (currentItem == 2){
                    startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
                    return@apply
                }
                currentItem += 1
            }
        }
        binding.tvOnboargingSkip.setOnClickListener {
            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
        }
    }

    private fun setNextButton(){
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

    private fun setStartButton(){
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

    private fun animateProgressIndicator(from : Int, to : Int){
        ValueAnimator.ofInt(from, to).apply {
            addUpdateListener {
                binding.lpiOnboarding.progress = it.animatedValue as Int
            }
            start()
        }
    }

}