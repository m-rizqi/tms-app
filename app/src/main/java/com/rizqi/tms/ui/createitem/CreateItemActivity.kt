package com.rizqi.tms.ui.createitem

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCreateItemBinding
import com.rizqi.tms.ui.hideLoading
import com.rizqi.tms.ui.showLoading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateItemBinding
    private lateinit var navHostFragment: NavHostFragment
    private val viewModel : CreateItemViewModel by viewModels()

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_create_item) as NavHostFragment
        navController = navHostFragment.navController

        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createItemUiState.collect { uiState ->

                    uiState.shouldShowLoading.let {
                        if (it) showLoading(binding.lCreateItemLoading)
                        else hideLoading(binding.lCreateItemLoading)
                    }

                    uiState.shouldChangeToNextStep?.let {
                        changeToStep(it)
                    }

                    uiState.generalMessage?.let {
                        Toast.makeText(this@CreateItemActivity, it.asString(this@CreateItemActivity), Toast.LENGTH_SHORT).show()
                        viewModel.reportGeneralMessageShown()
                    }

                }
            }
        }

        binding.btnCreateItemBack.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        if (viewModel.createItemUiState.value.currentStep == 1){
            finish()
        }else{
            changeToStep(viewModel.createItemUiState.value.currentStep - 1)
            when(viewModel.createItemUiState.value.currentStep){
                1 -> {
                    navController.navigate(Step2CreateItemFragmentDirections.step2ToStep1CreateItem())
                }
                2 -> {
                    navController.navigate(Step3CreateItemFragmentDirections.step3ToStep2CreateItem())
                }
            }
        }
    }

    private fun changeToStep(nextStep: Int) {
        if (nextStep > viewModel.createItemUiState.value.currentStep){
            when(nextStep){
                2 -> {
                    animateNextStep(binding.lpiCreateItemStep1ToStep2, binding.mcvCreateItemStep2, binding.tvCreateItem2, binding.tvCreateItemPrice)
                }
                3 -> {
                    animateNextStep(binding.lpiCreateItemStep2ToStep3, binding.mcvCreateItemStep3, binding.tvCreateItem3, binding.tvCreateItemMainPrice)
                }
            }
        }else{
            when(nextStep){
                2 -> {
                    animatePrevStep(binding.lpiCreateItemStep2ToStep3, binding.mcvCreateItemStep3, binding.tvCreateItem3, binding.tvCreateItemMainPrice)
                }
                1 -> {
                    animatePrevStep(binding.lpiCreateItemStep1ToStep2, binding.mcvCreateItemStep2, binding.tvCreateItem2, binding.tvCreateItemPrice)
                }
            }
        }
        viewModel.updateCurrentStep(nextStep)
    }

    private fun animateNextStep(
        linearProgressIndicator: LinearProgressIndicator,
        nextCardView : MaterialCardView,
        nextStepTextView : TextView,
        nextTitleTextView : TextView
    ){
        val bgAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.track_color),
            resources.getColor(R.color.primary_100)
        ).apply {
            addUpdateListener {
                nextCardView.setCardBackgroundColor(it.animatedValue as Int)
            }
        }
        val stepAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.primary_60),
            resources.getColor(R.color.white)
        ).apply {
            addUpdateListener {
                nextStepTextView.setTextColor(it.animatedValue as Int)
            }
        }
        val titleAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.black_60),
            resources.getColor(R.color.black_100)
        ).apply {
            addUpdateListener {
                nextTitleTextView.setTextColor(it.animatedValue as Int)
            }
        }

        ValueAnimator.ofInt(0, 100).apply {
            addUpdateListener {
                linearProgressIndicator.progress = it.animatedValue as Int
            }
            doOnEnd {
                AnimatorSet().apply {
                    playTogether(bgAnim, stepAnim, titleAnim)
                    start()
                }
            }
            start()
        }
    }

    private fun animatePrevStep(
        linearProgressIndicator: LinearProgressIndicator,
        prevCardView : MaterialCardView,
        prevStepTextView : TextView,
        prevTitleTextView : TextView
    ){
        val bgAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.primary_100),
            resources.getColor(R.color.track_color)
        ).apply {
            addUpdateListener {
                prevCardView.setCardBackgroundColor(it.animatedValue as Int)
            }
        }
        val stepAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.white),
            resources.getColor(R.color.primary_60)
        ).apply {
            addUpdateListener {
                prevStepTextView.setTextColor(it.animatedValue as Int)
            }
        }
        val titleAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.black_100),
            resources.getColor(R.color.black_60)
        ).apply {
            addUpdateListener {
                prevTitleTextView.setTextColor(it.animatedValue as Int)
            }
        }

        AnimatorSet().apply {
            playTogether(bgAnim, stepAnim, titleAnim)
            doOnEnd {
                ValueAnimator.ofInt(100, 0).apply {
                    addUpdateListener {
                        linearProgressIndicator.progress = it.animatedValue as Int
                    }
                    start()
                }
            }
            start()
        }

    }
}